/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.michaelfotiads.xkcdreader.data.db.dao.ComicsDao
import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity
import com.michaelfotiads.xkcdreader.net.api.ComicApi
import com.michaelfotiads.xkcdreader.net.api.model.ComicStrip
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

class ComicsRepo(
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

    fun loadLatestComic(): Single<RepoResult<ComicEntity>> {

        return comicApi.getLatest()
            .map { comicStrip ->
                val entity = upsertComicStripFromNet(comicStrip)
                RepoResult(entity)
            }
            .onErrorReturn { throwable ->
                RepoResult(dataSourceError = errorMapper.convert(throwable))
            }
    }

    fun loadComicWithId(comicId: Int): Single<RepoResult<ComicEntity>> {

        return Single.fromCallable {
            Optional.ofNullable(comicsDao.getForId(comicId))
        }
            .flatMap { optional ->
                if (optional.isPresent) {
                    val entity = optional.get()!!
                    Timber.i("Getting comic from DB $comicId")
                    Single.just(RepoResult(entity))
                } else {
                    comicApi.getForId(comicId = comicId.toString())
                        .map { comicStrip ->
                            val entity = upsertComicStripFromNet(comicStrip)
                            RepoResult(entity)
                        }
                        .onErrorReturn {
                            RepoResult(dataSourceError = errorMapper.convert(it))
                        }
                }
            }
    }

    fun toggleFavourite(comicEntity: ComicEntity, isFavourite: Boolean) {
        comicEntity.isFavourite = isFavourite
        comicsDao.upsert(comicEntity)
    }

    fun getFavourites(): LiveData<List<ComicEntity>> {
        return comicsDao.getAllFavouritesLiveData()
    }

    fun getForComicStripId(comicStripId: Int): Optional<ComicEntity?> {
        return Optional.ofNullable(comicsDao.getForId(comicStripId))
    }

    fun getPagedItems(): DataSource.Factory<Int, ComicEntity> {
        return comicsDao.getComicsPaged()
    }

    fun deleteData() {
        comicsDao.deleteAll()
    }

    private fun upsertComicStripFromNet(comicStrip: ComicStrip): ComicEntity {
        val entity = comicsMapper.convert(comicStrip)
        comicsDao.getForId(comicStrip.num)?.let { existingEntity ->
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
