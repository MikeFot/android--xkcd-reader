package com.michaelfotiads.xkcdreader.ui.fragment.favourites.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.michaelfotiads.xkcdreader.interactor.BaseRxInteractor
import com.michaelfotiads.xkcdreader.interactor.ToggleFavouriteInteractor
import com.michaelfotiads.xkcdreader.ui.config.PagesConfigProvider
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.interactor.LoadFavouritePagesInteractor
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.model.FavouritesAction
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip

class FavouritesViewModel(
    private val loadFavouritePagesInteractor: LoadFavouritePagesInteractor,
    private val toggleFavouriteInteractor: ToggleFavouriteInteractor,
    private val pagesConfigProvider: PagesConfigProvider
) : ViewModel() {

    private val interactors = listOf(
        loadFavouritePagesInteractor, toggleFavouriteInteractor
    )

    val actionLiveData = MutableLiveData<FavouritesAction>().apply { value = FavouritesAction.Idle }

    val pagedList by lazy {
        LivePagedListBuilder(
            loadFavouritePagesInteractor.getPages(),
            pagesConfigProvider.pagesCount
        )
            .setBoundaryCallback(object : PagedList.BoundaryCallback<UiComicStrip>() {
            })
            .build()
    }

    fun toggleFavourite(comicStripId: Int, isFavourite: Boolean) {
        toggleFavouriteInteractor.toggleFavourite(comicStripId, isFavourite)
    }

    fun showAboutDialog() {
        actionLiveData.postValue(FavouritesAction.ShowDialog(AppDialog.About))
    }

    override fun onCleared() {
        interactors.forEach(BaseRxInteractor::clear)
        super.onCleared()
    }
}
