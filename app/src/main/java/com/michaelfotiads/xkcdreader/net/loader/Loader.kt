/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.michaelfotiads.xkcdreader.data.db.dao.ComicsDao
import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity
import com.michaelfotiads.xkcdreader.net.api.ComicApi
import com.michaelfotiads.xkcdreader.net.api.model.ComicStrip
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceError
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceErrorKind
import com.michaelfotiads.xkcdreader.net.loader.error.mapper.RetrofitErrorMapper
import com.michaelfotiads.xkcdreader.net.loader.mapper.ComicsMapper
import com.michaelfotiads.xkcdreader.net.resolver.NetworkResolver
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java8.util.Optional
import retrofit2.Retrofit
import timber.log.Timber

class Loader(
    retrofit: Retrofit,
    private val comicsDao: ComicsDao,
    private val comicsMapper: ComicsMapper,
    private val networkResolver: NetworkResolver,
    private val errorMapper: RetrofitErrorMapper,
    private val scheduler: Scheduler
) {

    private var latestComicCall: Disposable? = null
    private var specificComicCall: Disposable? = null
    private val disposables = CompositeDisposable()
    private val comicApi = ComicApi(retrofit)

    fun loadLatestComic(): LiveData<RepoResult<ComicEntity>> {

        val liveData = MutableLiveData<RepoResult<ComicEntity>>()

        latestComicCall?.dispose()
        if (networkResolver.isConnected()) {
            latestComicCall = comicApi
                .getLatest()
                .subscribeOn(scheduler)
                .subscribe(
                    { it ->
                        val entity = upsertComicStripFromNet(it)
                        liveData.postValue(RepoResult(entity))
                    },
                    { liveData.postValue(RepoResult(dataSourceError = errorMapper.convert(it))) }
                )
        } else {
            // let the user know there has been an issue first
            liveData.postValue(
                RepoResult(
                    dataSourceError = DataSourceError(
                        "",
                        DataSourceErrorKind.NO_NETWORK
                    )
                )
            )

            // try showing something
            val optional =
                Single.fromCallable {
                    Optional.ofNullable(comicsDao.getAllComics())
                }.subscribeOn(scheduler)
                    .doOnSubscribe { disposables.add(it) }
                    .blockingGet()

            if (optional.isPresent && optional.get().isNotEmpty()) {
                Timber.i("Getting first comic from DB")
                liveData.postValue(RepoResult(optional.get()[0]))
            }
        }
        return liveData
    }

    fun loadComicWithId(comicId: Int): LiveData<RepoResult<ComicEntity>> {

        val liveData = MutableLiveData<RepoResult<ComicEntity>>()

        val optional =
            Single.fromCallable {
                Optional.ofNullable(comicsDao.getForId(comicId))
            }.subscribeOn(scheduler)
                .doOnSubscribe { disposables.add(it) }
                .blockingGet()
        if (optional.isPresent) {
            Timber.i("Getting comic from DB $comicId")
            liveData.postValue(RepoResult(optional.get()))
        } else {
            specificComicCall?.dispose()
            if (networkResolver.isConnected()) {
                specificComicCall = comicApi
                    .getForId(comicId = comicId.toString())
                    .subscribeOn(scheduler)
                    .subscribe(
                        {
                            val entity = upsertComicStripFromNet(it)
                            Timber.i("Got comic from Net $comicId")
                            liveData.postValue(RepoResult(entity))
                        },
                        {
                            Timber.e("Error fetching comic $comicId")
                            liveData.postValue(
                                RepoResult(
                                    dataSourceError = errorMapper.convert(it)
                                )
                            )
                        }
                    )
            } else {
                liveData.postValue(
                    RepoResult(
                        dataSourceError = DataSourceError(
                            "",
                            DataSourceErrorKind.NO_NETWORK
                        )
                    )
                )
            }
        }
        return liveData
    }

    fun toggleFavourite(comicId: Int, isFavourite: Boolean) {
        val optional =
            Single.fromCallable {
                Optional.ofNullable(comicsDao.getForId(comicId))
            }
                .doOnSubscribe { disposables.add(it) }
                .subscribeOn(scheduler).blockingGet()
        if (optional.isPresent) {
            Timber.i("Found comic id DB $comicId")
            Single.fromCallable {
                val updatedComic = optional.get()!!
                updatedComic.isFavourite = isFavourite
                Timber.d("New favourite value ${updatedComic.isFavourite}")
                comicsDao.upsert(updatedComic)
            }
                .doOnSubscribe { disposables.add(it) }
                .subscribeOn(scheduler)
                .subscribe()
        }
    }

    fun getFavourites(): LiveData<List<ComicEntity>> {
        return comicsDao.getAllFavouritesLiveData()
    }

    private fun upsertComicStripFromNet(it: ComicStrip): ComicEntity {
        val entity = comicsMapper.convert(it)
        comicsDao.getForId(it.num)?.let { existingEntity ->
            entity.isFavourite = existingEntity.isFavourite
        }
        comicsDao.upsert(entity)
        return entity
    }

    fun clearAllJobs() {
        latestComicCall?.dispose()
        specificComicCall?.dispose()
        disposables.clear()
    }
}
