/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michaelfotiads.xkcdreader.data.prefs.UserDataStore
import com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor.LoadComicPagesInteractor
import com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor.LoadSpecificComicInteractor
import com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor.ResetDataInteractor
import com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor.ToggleFavouriteInteractor
import javax.inject.Inject

class ComicsViewModelFactory @Inject constructor(
    private val loadComicPagesInteractor: LoadComicPagesInteractor,
    private val loadSpecificComicInteractor: LoadSpecificComicInteractor,
    private val toggleFavouriteInteractor: ToggleFavouriteInteractor,
    private val clearDataInteractor: ResetDataInteractor,
    private val dataStore: UserDataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ComicsViewModel(
            loadComicPagesInteractor,
            loadSpecificComicInteractor,
            toggleFavouriteInteractor,
            clearDataInteractor,
            dataStore) as T
    }
}
