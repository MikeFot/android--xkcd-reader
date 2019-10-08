/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import com.michaelfotiads.xkcdreader.di.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import es.dmoral.toasty.Toasty
import timber.log.Timber

class XkcdReaderApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        if (initLeakCanary()) {
            initLogging()
            initFresco()
            initStethoscope()
            initToasty()
        }
    }

    private fun initLeakCanary(): Boolean {
        return if (LeakCanary.isInAnalyzerProcess(this)) {
            false
        } else {
            LeakCanary.install(this)
            true
        }
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialised")
        } else {
            Timber.e("You should not be seeing this!")
        }
    }

    private fun initFresco() {
        Fresco.initialize(this)
    }

    private fun initStethoscope() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initToasty() {
        Toasty.Config.getInstance()
            .tintIcon(true)
            .apply()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}
