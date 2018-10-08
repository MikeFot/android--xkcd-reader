/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.main

import androidx.core.content.ContextCompat
import com.facebook.drawee.backends.pipeline.Fresco
import com.michaelfotiads.xkcdreader.BuildConfig
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import es.dmoral.toasty.Toasty
import timber.log.Timber

class XkcdReaderApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialised")
        } else {
            Timber.e("You should not be seeing this!")
        }
        Fresco.initialize(this)

        Toasty.Config.getInstance()
                .setErrorColor(ContextCompat.getColor(this, R.color.pink_800))
                .setInfoColor(ContextCompat.getColor(this, R.color.primary_dark))
                .setSuccessColor(ContextCompat.getColor(this, R.color.primary))
                .setWarningColor(ContextCompat.getColor(this, R.color.accent))
                .setTextColor(ContextCompat.getColor(this, R.color.white))
                .tintIcon(true)
                .setTextSize(12)
                .apply()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .application(this)
                .build()
    }
}
