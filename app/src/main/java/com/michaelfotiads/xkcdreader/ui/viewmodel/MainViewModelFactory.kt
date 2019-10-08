/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michaelfotiads.xkcdreader.data.prefs.DataStore
import com.michaelfotiads.xkcdreader.net.loader.Loader
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper

class MainViewModelFactory constructor(
    private val networkLoader: Loader,
    private val imageCountThreshold: Int,
    private val dataStore: DataStore,
    private val uiComicStripMapper: UiComicStripMapper,
    private val uiErrorMapper: UiErrorMapper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(
                networkLoader,
                imageCountThreshold,
                dataStore,
                uiComicStripMapper,
                uiErrorMapper) as T
    }
}
