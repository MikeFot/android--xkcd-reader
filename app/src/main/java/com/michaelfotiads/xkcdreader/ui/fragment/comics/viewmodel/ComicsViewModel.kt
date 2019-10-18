/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.michaelfotiads.xkcdreader.data.prefs.UserDataStore
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor.*
import com.michaelfotiads.xkcdreader.ui.fragment.comics.model.ComicAction
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip

private const val PAGES_SIZE = 12

class ComicsViewModel(
    loadComicPagesInteractor: LoadComicPagesInteractor,
    private val loadSpecificComicInteractor: LoadSpecificComicInteractor,
    private val toggleFavouriteInteractor: ToggleFavouriteInteractor,
    private val resetDataInteractor: ResetDataInteractor,
    private val dataStore: UserDataStore
) : ViewModel() {
    val pagedItems: LiveData<PagedList<UiComicStrip>>
    val actionLiveData = MutableLiveData<ComicAction>().apply { value = ComicAction.Idle }
    val lastLoadedIndex = MutableLiveData<Int>().apply { value = 0 }

    private val interactors = listOf(
        loadSpecificComicInteractor, toggleFavouriteInteractor, resetDataInteractor
    )
    private var searchQueryId = 0

    init {
        class UiComicStripBoundaryCallback : PagedList.BoundaryCallback<UiComicStrip>() {
            override fun onZeroItemsLoaded() {
                super.onZeroItemsLoaded()
                lastLoadedIndex.postValue(searchQueryId)
                searchQueryId = 0
            }

            override fun onItemAtEndLoaded(itemAtEnd: UiComicStrip) {
                super.onItemAtEndLoaded(itemAtEnd)
                if (itemAtEnd.number != 1) {
                    lastLoadedIndex.postValue(itemAtEnd.number - 1)
                }
            }
        }
        pagedItems = LivePagedListBuilder(loadComicPagesInteractor.loadData(), PAGES_SIZE)
            .setBoundaryCallback(UiComicStripBoundaryCallback())
            .build()
    }

    fun loadComic(comicStripId: Int) {
        loadSpecificComicInteractor.load(comicStripId, object : LoadSpecificComicInteractor.Callback {
            override fun onSuccess(uiComicStrip: UiComicStrip) {
                actionLiveData.postValue(ComicAction.ShowContent)
            }

            override fun onError(uiError: UiError) {
                actionLiveData.postValue(ComicAction.ShowError(uiError))
            }
        })
    }

    fun showSearch() {
        dataStore.maxStripIndex.let { index ->
            if (index > 0) {
                showSearchDialog(index)
            }
        }
    }

    fun setSearchParameter(comicStripId: Int) {
        searchQueryId = comicStripId
        refresh()
    }

    fun refresh() {
        loadSpecificComicInteractor.clear()
        resetDataInteractor.resetData()
    }

    private fun showSearchDialog(comicStripId: Int) {
        actionLiveData.postValue(ComicAction.ShowDialog(AppDialog.Search(comicStripId)))
    }

    fun showAboutDialog() {
        actionLiveData.postValue(ComicAction.ShowDialog(AppDialog.About))
    }

    fun clearError() {
        actionLiveData.postValue(null)
    }

    override fun onCleared() {
        interactors.forEach(BaseRxInteractor::clear)
        super.onCleared()
    }

    fun toggleFavourite(comicStripId: Int, isFavourite: Boolean) {
        toggleFavouriteInteractor.toggleFavourite(comicStripId, isFavourite)
    }
}
