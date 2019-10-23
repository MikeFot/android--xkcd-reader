package com.michaelfotiads.xkcdreader.ui.fragment.favourites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michaelfotiads.xkcdreader.interactor.ToggleFavouriteInteractor
import com.michaelfotiads.xkcdreader.ui.config.PagesConfigProvider
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.interactor.LoadFavouritePagesInteractor
import javax.inject.Inject

internal class FavouritesViewModelFactory @Inject constructor(
    private val loadFavouritePagesInteractor: LoadFavouritePagesInteractor,
    private val toggleFavouriteInteractor: ToggleFavouriteInteractor,
    private val pagesConfigProvider: PagesConfigProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavouritesViewModel(
            loadFavouritePagesInteractor,
            toggleFavouriteInteractor,
            pagesConfigProvider
        ) as T
    }
}
