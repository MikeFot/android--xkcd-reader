/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.michaelfotiads.xkcdreader.data.db.dao.ComicsDao
import com.michaelfotiads.xkcdreader.data.db.dao.PagesDao
import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity
import com.michaelfotiads.xkcdreader.data.db.entity.PageEntity

@Database(
    entities = [ComicEntity::class, PageEntity::class],
    version = 3
)
abstract class ComicsDatabase : RoomDatabase() {

    abstract fun comicsDao(): ComicsDao

    abstract fun pagesDao(): PagesDao
}
