/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.di

import android.app.Application
import androidx.room.Room
import com.michaelfotiads.xkcdreader.data.db.ComicsDatabase
import com.michaelfotiads.xkcdreader.data.prefs.DataStore
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

private const val DB_NAME = "key.db.name"

@Module
class DataModule {

    @Provides internal fun providesDataStore(application: Application) = DataStore(application)

    @Provides @Named(DB_NAME) internal fun providesDatabaseFileName() = "xkcd_comics_db"

    @Singleton @Provides internal fun providesInMemoryDatabase(
        application: Application,
        @Named(DB_NAME) dbName: String
    ): ComicsDatabase {
        return Room.databaseBuilder(application, ComicsDatabase::class.java, dbName)
            .fallbackToDestructiveMigration().build()
    }

    @Provides internal fun providesComicsDao(database: ComicsDatabase) = database.comicsDao()
}