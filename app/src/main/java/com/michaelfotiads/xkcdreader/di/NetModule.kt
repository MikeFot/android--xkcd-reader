/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.di

import android.app.Application
import com.google.gson.Gson
import com.michaelfotiads.xkcdreader.net.factory.OkHttpBuilderFactory
import com.michaelfotiads.xkcdreader.net.factory.RetrofitBuilderFactory
import com.michaelfotiads.xkcdreader.repo.error.mapper.RetrofitErrorMapper
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
class NetModule {

    @Provides @Singleton internal fun providesCache(application: Application): Cache {
        return Cache(application.cacheDir, 10 * 1024 * 1024)
    }

    @Provides @Singleton internal fun providesOkHttpClient(
        cache: Cache,
        @Named(NAME_DEBUG) isDebugEnabled: Boolean
    ): OkHttpClient {
        return OkHttpBuilderFactory.providesOkHttpClientBuilder(cache, isDebugEnabled).build()
    }

    @Provides internal fun providesRetrofit(
        @Named(NAME_URL) baseUrl: String,
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return RetrofitBuilderFactory.providesRetrofit(baseUrl, gson, okHttpClient).build()
    }

    @Provides internal fun providesRetrofitErrorMapper() = RetrofitErrorMapper()
}
