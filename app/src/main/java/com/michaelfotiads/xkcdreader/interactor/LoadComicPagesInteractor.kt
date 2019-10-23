package com.michaelfotiads.xkcdreader.interactor

import androidx.paging.DataSource
import com.michaelfotiads.xkcdreader.repo.ComicsRepo
import com.michaelfotiads.xkcdreader.scheduler.ExecutionThreads
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import io.reactivex.Single
import javax.inject.Inject

class LoadComicPagesInteractor @Inject constructor(
    private val comicsRepo: ComicsRepo,
    private val uiComicStripMapper: UiComicStripMapper,
    private val executionThreads: ExecutionThreads
) : BaseRxInteractor() {

    fun getPages(): DataSource.Factory<Int, UiComicStrip> {
        return Single.fromCallable {
            comicsRepo.getPagedComics()
        }
            .doOnSubscribe(this::addDisposable)
            .subscribeOn(executionThreads.jobExecutionThread)
            .map { pagedItems ->
                pagedItems.map { comicEntity ->
                    uiComicStripMapper.convert(comicEntity)
                }
            }
            .blockingGet()
    }
}
