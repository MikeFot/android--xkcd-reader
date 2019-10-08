/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.di

import com.michaelfotiads.xkcdreader.data.prefs.DataStore
import com.michaelfotiads.xkcdreader.net.loader.Loader
import com.michaelfotiads.xkcdreader.ui.MainActivity
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.intent.IntentDispatcher
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
internal class MainActivityModule {

    companion object {
        const val NAME_IMAGE_THRESHOLD = "image-count-threshold"
    }

    @Provides
    fun providesResources(mainActivity: MainActivity) = mainActivity.resources

    @Provides
    @Named(NAME_IMAGE_THRESHOLD)
    fun providesImageCountThreshold() = 6

    @Provides
    fun providesIntentDispatcher(mainActivity: MainActivity) = IntentDispatcher(mainActivity)

    @Provides
    fun providesMainViewModelFactory(
        networkLoader: Loader,
        @Named(NAME_IMAGE_THRESHOLD) imageCountThreshold: Int,
        dataStore: DataStore,
        uiComicStripMapper: UiComicStripMapper,
        uiErrorMapper: UiErrorMapper
    ) = MainViewModelFactory(
        networkLoader,
        imageCountThreshold,
        dataStore,
        uiComicStripMapper,
        uiErrorMapper
    )
}
