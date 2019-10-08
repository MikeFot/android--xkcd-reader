/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michaelfotiads.xkcdreader.data.prefs.DataStore
import com.michaelfotiads.xkcdreader.net.loader.Loader
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import timber.log.Timber

class MainViewModel(
    private val loader: Loader,
    private val imageCountThreshold: Int,
    private val dataStore: DataStore,
    private val uiComicStripMapper: UiComicStripMapper,
    private val uiErrorMapper: UiErrorMapper
) : ViewModel() {

    val resultsData = MediatorLiveData<UiComicStrip>()
    val favouritesData = MediatorLiveData<List<UiComicStrip>>()
    val errorData = MutableLiveData<UiError>()
    val dialogData = MutableLiveData<AppDialog>()
    val resetAdapterData = MutableLiveData<Boolean>()

    init {

        favouritesData.addSource(loader.getFavourites()) { entities ->
            if (entities != null) {
                favouritesData.postValue(uiComicStripMapper.convert(entities))
            } else {
                favouritesData.postValue(listOf())
            }
        }
    }

    fun loadInitialData() {

        val currentStrip = dataStore.currentStrip
        if (currentStrip == 0) {
            Timber.d("Loading initial data")
            resultsData.addSource(loader.loadLatestComic()) { result ->
                when {
                    result.hasError() -> errorData.postValue(uiErrorMapper.convert(result.dataSourceError!!))
                    result.hasPayload() -> {
                        val uiItem = uiComicStripMapper.convert(result.payload!!)
                        dataStore.currentStrip = uiItem.number
                        if (uiItem.number > dataStore.maxStripIndex) {
                            Timber.d("Max Strip is ${uiItem.number}")
                            dataStore.maxStripIndex = uiItem.number
                        }
                        Timber.d("Current Strip is ${uiItem.number}")
                        resultsData.postValue(uiItem)
                    }
                }
            }
        } else {
            Timber.d("Loading initial data")
            loadSpecificItem(currentStrip)
        }
    }

    fun loadSpecificItem(comicStripId: Int) {
        Timber.d("Loading specific item $comicStripId")
        resultsData.addSource(loader.loadComicWithId(comicStripId)) { result ->
            when {
                result.hasError() -> errorData.postValue(uiErrorMapper.convert(result.dataSourceError!!))
                result.hasPayload() -> {
                    val uiItem = uiComicStripMapper.convert(result.payload!!)
                    if (uiItem.number > dataStore.maxStripIndex) {
                        Timber.d("Load additional - Max Strip is ${uiItem.number}")
                        dataStore.maxStripIndex = uiItem.number
                    }
                    resultsData.postValue(uiItem)
                }
            }
        }
    }

    fun loadAdditionalData(visibleItems: Int) {

        val currentStrip = dataStore.currentStrip
        val nextItem = currentStrip - visibleItems

        if (nextItem != currentStrip && nextItem > 0 && visibleItems in 0 until imageCountThreshold) {
            Timber.d("Loading additional data for $currentStrip next item $nextItem and count $visibleItems")
            resultsData.addSource(loader.loadComicWithId(nextItem)) { result ->
                when {
                    result.hasError() -> errorData.postValue(uiErrorMapper.convert(result.dataSourceError!!))
                    result.hasPayload() -> {
                        val uiItem = uiComicStripMapper.convert(result.payload!!)
                        if (uiItem.number > dataStore.maxStripIndex) {
                            Timber.d("Load additional - Max Strip is ${uiItem.number}")
                            dataStore.maxStripIndex = uiItem.number
                        }
                        resultsData.postValue(uiItem)
                    }
                }
            }
        } else {
            Timber.w("Cannot load additional data for $currentStrip and count $visibleItems")
        }
    }

    fun showSearch() {
        dataStore.maxStripIndex.run {
            if (this > 0) {
                showSearchDialog(this)
            }
        }
    }

    fun resetData() {
        resetAdapterData.postValue(true)
        dataStore.currentStrip = 0
        Timber.d("Data has been reset")
    }

    fun setSearchParameter(stripNumber: Int) {
        resetAdapterData.postValue(true)
        dataStore.currentStrip = stripNumber
        loadSpecificItem(stripNumber)
    }

    fun decrementCurrentStrip() {
        dataStore.currentStrip = dataStore.currentStrip - 1
    }

    fun showSearchDialog(id: Int) {
        dialogData.postValue(AppDialog.Search(id))
    }

    fun showAboutDialog() {
        dialogData.postValue(AppDialog.About)
    }

    fun clearError() {
        errorData.value = null
        Timber.d("Error cleared")
    }

    override fun onCleared() {
        loader.clearAllJobs()
        dataStore.currentStrip = 0
        Timber.d("Clearing ViewModel")
        super.onCleared()
    }

    fun toggleFavourite(id: Int, isFavourite: Boolean) {
        loader.toggleFavourite(id, isFavourite)
    }
}
