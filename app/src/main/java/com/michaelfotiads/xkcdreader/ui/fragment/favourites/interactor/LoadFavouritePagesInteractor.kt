package com.michaelfotiads.xkcdreader.ui.fragment.favourites.interactor

import androidx.paging.DataSource
import com.michaelfotiads.xkcdreader.repo.ComicsRepo
import com.michaelfotiads.xkcdreader.scheduler.ExecutionThreads
import com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor.BaseRxInteractor
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.model.UiComicStripMapper
import io.reactivex.Single
import javax.inject.Inject

class LoadFavouritePagesInteractor @Inject constructor(
    private val comicsRepo: ComicsRepo,
    private val uiComicStripMapper: UiComicStripMapper,
    private val executionThreads: ExecutionThreads
) : BaseRxInteractor() {

    fun getPages(): DataSource.Factory<Int, UiComicStrip> {
        return Single.fromCallable {
            comicsRepo.getPagedFavourites()
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
