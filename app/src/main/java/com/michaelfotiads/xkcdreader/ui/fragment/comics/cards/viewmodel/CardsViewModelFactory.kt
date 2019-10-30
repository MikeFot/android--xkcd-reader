/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics.cards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michaelfotiads.xkcdreader.data.prefs.UserDataStore
import com.michaelfotiads.xkcdreader.interactor.LoadCardPagesInteractor
import com.michaelfotiads.xkcdreader.interactor.LoadSpecificComicInteractor
import com.michaelfotiads.xkcdreader.interactor.ResetCardPagesInteractor
import com.michaelfotiads.xkcdreader.interactor.SearchProcessInteractor
import com.michaelfotiads.xkcdreader.interactor.ToggleFavouriteInteractor
import com.michaelfotiads.xkcdreader.ui.config.PagesConfigProvider
import javax.inject.Inject

class CardsViewModelFactory @Inject constructor(
    private val loadCardPagesInteractor: LoadCardPagesInteractor,
    private val loadSpecificComicInteractor: LoadSpecificComicInteractor,
    private val toggleFavouriteInteractor: ToggleFavouriteInteractor,
    private val resetCardPagesInteractor: ResetCardPagesInteractor,
    private val searchProcessInteractor: SearchProcessInteractor,
    private val pagesConfigProvider: PagesConfigProvider,
    private val dataStore: UserDataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CardsViewModel(
            loadCardPagesInteractor,
            loadSpecificComicInteractor,
            toggleFavouriteInteractor,
            resetCardPagesInteractor,
            searchProcessInteractor,
            pagesConfigProvider,
            dataStore) as T
    }
}
