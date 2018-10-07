/*
 * Developed by Michail Fotiadis on 07/10/18 18:05.
 * Last modified 07/10/18 18:05.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.ui.di

import com.michaelfotiads.xkcdreader.net.loader.NetworkLoader
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    internal fun providesUiErrorMapper() = UiErrorMapper()

    @Provides
    internal fun providesUiComicStripMapper() = UiComicStripMapper()

    @Provides
    internal fun providesMainViewModelFactory(
        networkLoader: NetworkLoader,
        uiComicStripMapper: UiComicStripMapper,
        uiErrorMapper: UiErrorMapper
    ) =
            MainViewModelFactory(networkLoader, uiComicStripMapper, uiErrorMapper)
}