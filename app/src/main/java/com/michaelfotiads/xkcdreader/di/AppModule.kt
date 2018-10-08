/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.michaelfotiads.xkcdreader.BuildConfig
import com.michaelfotiads.xkcdreader.data.DataStore
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    internal fun providesContext(application: Application): Context {
        return application
    }

    @Provides
    internal fun providesDebugFlag(): Boolean {
        return BuildConfig.DEBUG
    }

    @Provides
    @Named("base-url")
    internal fun providesBaseUrl(): String {
        return "https://xkcd.com"
    }

    @Provides
    internal fun providesGson(): Gson {
        return GsonBuilder().setPrettyPrinting().create()
    }

    @Provides
    internal fun providesExecutionScheduler() = Schedulers.io()

    @Provides
    internal fun providesDataStore(context: Context) = DataStore(context)
}