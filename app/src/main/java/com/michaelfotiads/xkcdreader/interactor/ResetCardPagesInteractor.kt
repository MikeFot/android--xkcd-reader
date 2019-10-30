package com.michaelfotiads.xkcdreader.interactor

import com.michaelfotiads.xkcdreader.repo.ComicsRepo
import com.michaelfotiads.xkcdreader.scheduler.ExecutionThreads
import io.reactivex.Completable
import javax.inject.Inject

class ResetCardPagesInteractor @Inject constructor(
    private val loader: ComicsRepo,
    private val executionThreads: ExecutionThreads
) : BaseRxInteractor() {

    fun resetData() {
        addDisposable(
            Completable.fromAction {
                loader.deleteCardPageData()
            }
                .subscribeOn(executionThreads.jobExecutionThread)
                .subscribe()
        )
    }
}
