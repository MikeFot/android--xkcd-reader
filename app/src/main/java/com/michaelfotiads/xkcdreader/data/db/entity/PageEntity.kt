package com.michaelfotiads.xkcdreader.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "pages_table", foreignKeys = [ForeignKey(
    entity = ComicEntity::class,
    parentColumns = arrayOf("num"),
    childColumns = arrayOf("comicStripId"))])
data class PageEntity(
    @PrimaryKey(autoGenerate = false) var comicStripId: Int
)
