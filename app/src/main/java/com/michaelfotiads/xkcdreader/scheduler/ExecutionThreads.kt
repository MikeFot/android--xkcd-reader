package com.michaelfotiads.xkcdreader.scheduler

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ExecutionThreads @Inject constructor(){
    val jobExecutionThread = Schedulers.io()
    val postExecutionThread = AndroidSchedulers.mainThread()
}