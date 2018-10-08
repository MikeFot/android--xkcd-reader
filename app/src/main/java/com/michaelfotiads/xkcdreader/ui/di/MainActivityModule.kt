/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.di

import com.michaelfotiads.xkcdreader.data.DataStore
import com.michaelfotiads.xkcdreader.net.loader.NetworkLoader
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MainActivityModule {

    companion object {
        const val NAME_IMAGE_THRESHOLD = "image-count-threshold"
    }

    @Provides
    internal fun providesUiErrorMapper() = UiErrorMapper()

    @Provides
    internal fun providesUiComicStripMapper() = UiComicStripMapper()

    @Provides
    @Named(NAME_IMAGE_THRESHOLD)
    internal fun providesImageCountThreshold() = 6

    @Provides
    internal fun providesMainViewModelFactory(
            networkLoader: NetworkLoader,
            @Named(NAME_IMAGE_THRESHOLD) imageCountThreshold: Int,
            dataStore: DataStore,
            uiComicStripMapper: UiComicStripMapper,
            uiErrorMapper: UiErrorMapper
    ) = MainViewModelFactory(
            networkLoader,
            imageCountThreshold,
            dataStore,
            uiComicStripMapper,
            uiErrorMapper)
}