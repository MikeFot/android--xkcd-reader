package com.michaelfotiads.xkcdreader.ui.fragment.comics.interactor

import com.michaelfotiads.xkcdreader.net.loader.ComicsRepo
import com.michaelfotiads.xkcdreader.scheduler.ExecutionThreads
import io.reactivex.Completable
import javax.inject.Inject

class ResetDataInteractor @Inject constructor(
    private val loader: ComicsRepo,
    private val executionThreads: ExecutionThreads
) : BaseRxInteractor() {

    fun resetData() {
        addDisposable(
            Completable.fromAction {
                loader.deleteData()
            }
                .subscribeOn(executionThreads.jobExecutionThread)
                .subscribe()
        )
    }
}