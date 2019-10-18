/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.di

import com.michaelfotiads.xkcdreader.data.db.dao.ComicsDao
import com.michaelfotiads.xkcdreader.net.loader.ComicsRepo
import com.michaelfotiads.xkcdreader.net.loader.error.mapper.RetrofitErrorMapper
import com.michaelfotiads.xkcdreader.net.loader.mapper.ComicsMapper
import com.michaelfotiads.xkcdreader.net.resolver.NetworkResolver
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import retrofit2.Retrofit

@Module
class LoaderModule {

    @Provides
    internal fun providesComicsMapper() = ComicsMapper()

    @Provides
    internal fun providesLoader(
        retrofit: Retrofit,
        comicsDao: ComicsDao,
        comicMapper: ComicsMapper,
        networkResolver: NetworkResolver,
        retrofitErrorMapper: RetrofitErrorMapper,
        scheduler: Scheduler
    ): ComicsRepo {
        return ComicsRepo(
            retrofit,
            comicsDao,
            comicMapper,
            networkResolver,
            retrofitErrorMapper,
            scheduler
        )
    }
}
