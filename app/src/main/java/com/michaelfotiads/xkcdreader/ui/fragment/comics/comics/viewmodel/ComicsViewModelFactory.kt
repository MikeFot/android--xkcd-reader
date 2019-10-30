/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics.comics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michaelfotiads.xkcdreader.interactor.LoadComicPagesInteractor
import com.michaelfotiads.xkcdreader.interactor.LoadSpecificComicInteractor
import com.michaelfotiads.xkcdreader.interactor.ToggleFavouriteInteractor
import com.michaelfotiads.xkcdreader.ui.config.PagesConfigProvider
import javax.inject.Inject

class ComicsViewModelFactory @Inject constructor(
    private val loadComicPagesInteractor: LoadComicPagesInteractor,
    private val loadSpecificComicInteractor: LoadSpecificComicInteractor,
    private val toggleFavouriteInteractor: ToggleFavouriteInteractor,
    private val pagesConfigProvider: PagesConfigProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ComicsViewModel(
            loadComicPagesInteractor,
            loadSpecificComicInteractor,
            toggleFavouriteInteractor,
            pagesConfigProvider) as T
    }
}
