package com.michaelfotiads.xkcdreader.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.michaelfotiads.xkcdreader.data.db.entity.PageEntity
import com.michaelfotiads.xkcdreader.data.db.entity.PageWithComic

@Dao
interface PagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(pageEntity: PageEntity)

    @Query("DELETE FROM pages_table")
    fun deleteAll()

    @Query("SELECT * from pages_table, comics_table where num = comicStripId ORDER BY comicStripId DESC")
    fun getAllPages(): List<PageWithComic>

    @Query("SELECT * from pages_table, comics_table where num = comicStripId ORDER BY comicStripId DESC")
    fun getComicsPaged(): DataSource.Factory<Int, PageWithComic>
}
