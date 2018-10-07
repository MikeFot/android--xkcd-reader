/*
 * Developed by Michail Fotiadis on 07/10/18 18:04.
 * Last modified 07/10/18 18:04.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.michaelfotiads.xkcdreader.net.loader.NetworkLoader
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper

class MainViewModelFactory constructor(
    private val networkLoader: NetworkLoader,
    private val uiComicStripMapper: UiComicStripMapper,
    private val uiErrorMapper: UiErrorMapper
)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(networkLoader, uiComicStripMapper, uiErrorMapper) as T
    }
}