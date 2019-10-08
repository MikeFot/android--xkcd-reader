/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.michaelfotiads.xkcdreader.data.db.dao.ComicsDao
import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity

@Database(
    entities = [ComicEntity::class], version = 2
)
abstract class ComicsDatabase : RoomDatabase() {

    abstract fun comicsDao(): ComicsDao
}
