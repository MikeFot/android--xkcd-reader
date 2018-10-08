/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.michaelfotiads.xkcdreader.net.api.ComicApi
import com.michaelfotiads.xkcdreader.net.api.model.ComicStrip
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceError
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceErrorKind
import com.michaelfotiads.xkcdreader.net.loader.error.mapper.RetrofitErrorMapper
import com.michaelfotiads.xkcdreader.net.resolver.NetworkResolver
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit

class NetworkLoader(
    retrofit: Retrofit,
    private val networkResolver: NetworkResolver,
    private val errorMapper: RetrofitErrorMapper,
    private val scheduler: Scheduler
) {

    private var latestComicCall: Disposable? = null
    private var specificComicCall: Disposable? = null
    private val comicApi = ComicApi(retrofit)

    fun loadLatestComic(): LiveData<RepoResult<ComicStrip>> {

        val liveData = MutableLiveData<RepoResult<ComicStrip>>()

        latestComicCall?.dispose()
        if (networkResolver.isConnected()) {
            latestComicCall = comicApi
                    .getLatest()
                    .subscribeOn(scheduler)
                    .subscribe(
                            { liveData.postValue(RepoResult(it)) },
                            { liveData.postValue(RepoResult(dataSourceError = errorMapper.convert(it))) }
                    )
        } else {
            liveData.postValue(RepoResult(dataSourceError = DataSourceError("",
                                                                            DataSourceErrorKind.NO_NETWORK)))
        }
        return liveData
    }

    fun loadComicWithId(comicId: Int): LiveData<RepoResult<ComicStrip>> {

        val liveData = MutableLiveData<RepoResult<ComicStrip>>()

        specificComicCall?.dispose()
        if (networkResolver.isConnected()) {
            specificComicCall = comicApi
                    .getForId(comicId = comicId.toString())
                    .subscribeOn(scheduler)
                    .subscribe(
                            { liveData.postValue(RepoResult(it)) },
                            {
                                liveData.postValue(RepoResult(dataSourceError = errorMapper.convert(
                                        it)))
                            }
                    )
        } else {
            liveData.postValue(RepoResult(dataSourceError = DataSourceError("",
                                                                            DataSourceErrorKind.NO_NETWORK)))
        }

        return liveData
    }

    fun clearAllJobs() {
        latestComicCall?.dispose()
        specificComicCall?.dispose()
    }
}