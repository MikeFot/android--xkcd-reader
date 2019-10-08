/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.michaelfotiads.xkcdreader.BuildConfig
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

const val NAME_DEBUG = "named.is_debug_enabled"
const val NAME_URL = "key.base.url"

@Module
class AppModule {

    @Provides
    @Singleton
    internal fun providesApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Named(NAME_DEBUG)
    internal fun providesDebugFlag(): Boolean {
        return BuildConfig.DEBUG
    }

    @Provides
    @Named(NAME_URL)
    internal fun providesBaseUrl(): String {
        return "https://xkcd.com"
    }

    @Provides
    internal fun providesGson(): Gson {
        return GsonBuilder().setPrettyPrinting().create()
    }

    @Provides
    internal fun providesExecutionScheduler() = Schedulers.io()
}
