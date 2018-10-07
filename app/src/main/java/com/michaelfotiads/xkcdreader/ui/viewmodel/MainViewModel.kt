/*
 * Developed by Michail Fotiadis on 07/10/18 18:07.
 * Last modified 07/10/18 18:07.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michaelfotiads.xkcdreader.net.loader.NetworkLoader
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper

class MainViewModel(
    private val networkLoader: NetworkLoader,
    private val uiComicStripMapper: UiComicStripMapper,
    private val uiErrorMapper: UiErrorMapper
) : ViewModel() {

    val resultsData = MediatorLiveData<UiComicStrip>()
    val errorData = MutableLiveData<UiError>()

    fun loadInitialData() {

        resultsData.addSource(networkLoader.loadLatestComic()) { result ->
            when {
                result.hasError() -> errorData.postValue(uiErrorMapper.convert(result.dataSourceError!!))
                result.hasPayload() -> resultsData.postValue(uiComicStripMapper.convert(result.payload!!))
            }
        }
    }

    fun loadAdditionalData(number: Int) {
        resultsData.addSource(networkLoader.loadComicWithId(number)) { result ->
            when {
                result.hasError() -> errorData.postValue(uiErrorMapper.convert(result.dataSourceError!!))
                result.hasPayload() -> resultsData.postValue(uiComicStripMapper.convert(result.payload!!))
            }
        }
    }

    override fun onCleared() {
        networkLoader.clearAllJobs()
        super.onCleared()
    }
}