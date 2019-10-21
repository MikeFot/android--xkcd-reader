package com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor

import com.michaelfotiads.xkcdreader.repo.ComicsRepo
import com.michaelfotiads.xkcdreader.scheduler.ExecutionThreads
import io.reactivex.Completable
import javax.inject.Inject

class ResetPagesInteractor @Inject constructor(
    private val loader: ComicsRepo,
    private val executionThreads: ExecutionThreads
) : BaseRxInteractor() {

    fun resetData() {
        addDisposable(
            Completable.fromAction {
                loader.deletePageData()
            }
                .subscribeOn(executionThreads.jobExecutionThread)
                .subscribe()
        )
    }
}
