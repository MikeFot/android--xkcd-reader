package com.michaelfotiads.xkcdreader.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.michaelfotiads.xkcdreader.data.db.entity.CardPageEntity
import com.michaelfotiads.xkcdreader.data.db.entity.CardPageWithComic

@Dao
interface CardPagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(pageEntity: CardPageEntity)

    @Query("DELETE FROM card_pages_table")
    fun deleteAll()

    @Query("SELECT * from card_pages_table, comics_table where num = comicStripId ORDER BY comicStripId DESC")
    fun getAllPages(): List<CardPageWithComic>

    @Query("SELECT * from card_pages_table, comics_table where num = comicStripId ORDER BY comicStripId DESC")
    fun getComicsPaged(): DataSource.Factory<Int, CardPageWithComic>
}
