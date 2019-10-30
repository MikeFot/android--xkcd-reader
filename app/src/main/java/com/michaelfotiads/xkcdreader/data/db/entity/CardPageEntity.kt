package com.michaelfotiads.xkcdreader.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "card_pages_table", foreignKeys = [ForeignKey(
    entity = ComicEntity::class,
    parentColumns = arrayOf("num"),
    childColumns = arrayOf("comicStripId"))])
data class CardPageEntity(
    @PrimaryKey(autoGenerate = false) var comicStripId: Int
)
