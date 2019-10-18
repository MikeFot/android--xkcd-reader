package com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor

import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity
import com.michaelfotiads.xkcdreader.data.prefs.UserDataStore
import com.michaelfotiads.xkcdreader.net.loader.ComicsRepo
import com.michaelfotiads.xkcdreader.net.loader.RepoResult
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceError
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceErrorKind
import com.michaelfotiads.xkcdreader.net.resolver.NetworkResolver
import com.michaelfotiads.xkcdreader.scheduler.ExecutionThreads
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.error.UiErrorMapper
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import io.reactivex.Single
import javax.inject.Inject

class LoadSpecificComicInteractor @Inject constructor(
    private val comicsRepo: ComicsRepo,
    private val dataStore: UserDataStore,
    private val networkResolver: NetworkResolver,
    private val uiComicStripMapper: UiComicStripMapper,
    private val uiErrorMapper: UiErrorMapper,
    private val executionThreads: ExecutionThreads
) : BaseRxInteractor() {

    interface Callback {
        fun onSuccess(uiComicStrip: UiComicStrip)
        fun onError(uiError: UiError)
    }

    fun load(comicStripId: Int, callback: Callback) {

        if (networkResolver.isConnected()) {
            val operation = if (comicStripId <= 0) {
                getLatestComicOperation(callback)
            } else {
                getSpecificComicOperation(comicStripId, callback)
            }
            addDisposable(operation
                .subscribeOn(executionThreads.jobExecutionThread)
                .subscribe()
            )
        } else {
            callback.onError(uiErrorMapper.convert(DataSourceError("", DataSourceErrorKind.NO_NETWORK)))
        }
    }

    private fun getLatestComicOperation(callback: Callback): Single<RepoResult<ComicEntity>> {
        return comicsRepo.loadLatestComic()
            .doOnSuccess { result ->
                when {
                    result.dataSourceError != null ->
                        callback.onError(uiErrorMapper.convert(result.dataSourceError))
                    result.payload != null -> {
                        val uiItem = uiComicStripMapper.convert(result.payload)
                        if (uiItem.number > dataStore.maxStripIndex) {
                            dataStore.maxStripIndex = uiItem.number
                        }
                        callback.onSuccess(uiItem)
                    }
                }
            }
    }

    private fun getSpecificComicOperation(comicStripId: Int, callback: Callback): Single<RepoResult<ComicEntity>> {
        return comicsRepo.loadComicWithId(comicStripId)
            .doOnSuccess { result ->
                when {
                    result.dataSourceError != null ->
                        callback.onError(uiErrorMapper.convert(result.dataSourceError))
                    result.payload != null -> {
                        val uiItem = uiComicStripMapper.convert(result.payload)
                        if (uiItem.number > dataStore.maxStripIndex) {
                            dataStore.maxStripIndex = uiItem.number
                        }
                        callback.onSuccess(uiItem)
                    }
                }
            }
    }

}