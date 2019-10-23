package com.michaelfotiads.xkcdreader.net.factory

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal object OkHttpBuilderFactory {

    fun providesOkHttpClientBuilder(cache: Cache, isDebugEnabled: Boolean): OkHttpClient.Builder {
        return OkHttpClient().newBuilder().apply {
            val loggingInterceptor = HttpLoggingInterceptor()
            val level = when {
                isDebugEnabled -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
            loggingInterceptor.level = level
            this.addInterceptor(loggingInterceptor)
            if (isDebugEnabled) {
                this.addNetworkInterceptor(StethoInterceptor())
            }
            this.cache(cache)
        }
    }
}
