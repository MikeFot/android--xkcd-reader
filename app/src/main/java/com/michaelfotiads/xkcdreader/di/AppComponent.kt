/*
 * Developed by Michail Fotiadis on 07/10/18 18:03.
 * Last modified 07/10/18 18:03.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.di

import android.app.Application
import com.michaelfotiads.xkcdreader.main.XkcdReaderApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityBuilderModule::class,
    AppModule::class,
    NetModule::class
])
interface AppComponent : AndroidInjector<XkcdReaderApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
