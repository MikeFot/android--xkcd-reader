/*
 * Developed by Michail Fotiadis on 07/10/18 18:03.
 * Last modified 07/10/18 18:03.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.michaelfotiads.xkcdreader.net.error.RxErrorHandlingCallAdapterFactory
import com.michaelfotiads.xkcdreader.net.loader.NetworkLoader
import com.michaelfotiads.xkcdreader.net.loader.error.mapper.RetrofitErrorMapper
import com.michaelfotiads.xkcdreader.net.resolver.NetworkResolver
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetModule {

    @Provides
    @Singleton
    internal fun providesOkHttp(isDebugEnabled: Boolean): OkHttpClient {
        val okHttpBuilder = OkHttpClient().newBuilder()
        val loggingInterceptor = HttpLoggingInterceptor()
        val level = when {
            isDebugEnabled -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
        loggingInterceptor.level = level
        okHttpBuilder.addInterceptor(loggingInterceptor)
        return okHttpBuilder.build()
    }

    @Provides
    internal fun providesRetrofit(
        @Named("base-url") baseUrl: String,
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Provides
    internal fun providesNetworkResolver(context: Context): NetworkResolver {

        return object : NetworkResolver {
            override fun isConnected(): Boolean {
                val connectivityManager =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }
    }

    @Provides
    internal fun providesRetrofitErrorMapper() = RetrofitErrorMapper()

    @Provides
    internal fun providesNetworkLoader(
        retrofit: Retrofit,
        networkResolver: NetworkResolver,
        retrofitErrorMapper: RetrofitErrorMapper,
        scheduler: Scheduler
    ): NetworkLoader {
        return NetworkLoader(retrofit, networkResolver, retrofitErrorMapper, scheduler)
    }
}