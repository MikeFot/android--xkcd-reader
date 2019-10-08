/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity

@Dao
interface ComicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(comic: ComicEntity)

    @Query("DELETE FROM comics_table")
    fun deleteAll()

    @Query("SELECT * from comics_table ORDER BY num DESC")
    fun getAllComics(): List<ComicEntity>

    @Query("SELECT * from comics_table where isFavourite = 1 ORDER BY num DESC")
    fun getAllFavouritesLiveData(): LiveData<List<ComicEntity>>

    @Query("SELECT * FROM comics_table where num = :num")
    fun getForId(num: Int): ComicEntity?
}
