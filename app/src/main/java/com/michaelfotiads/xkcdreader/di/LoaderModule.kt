/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.di

import com.michaelfotiads.xkcdreader.data.db.dao.CardPagesDao
import com.michaelfotiads.xkcdreader.data.db.dao.ComicsDao
import com.michaelfotiads.xkcdreader.repo.ComicsRepo
import com.michaelfotiads.xkcdreader.repo.error.mapper.RetrofitErrorMapper
import com.michaelfotiads.xkcdreader.repo.mapper.ComicsMapper
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class LoaderModule {

    @Provides
    internal fun providesComicsMapper(): ComicsMapper {
        return ComicsMapper()
    }

    @Provides
    internal fun providesLoader(
        retrofit: Retrofit,
        comicsDao: ComicsDao,
        cardPagesDao: CardPagesDao,
        comicMapper: ComicsMapper,
        retrofitErrorMapper: RetrofitErrorMapper
    ): ComicsRepo {
        return ComicsRepo(
            retrofit,
            comicsDao,
            cardPagesDao,
            comicMapper,
            retrofitErrorMapper
        )
    }
}
