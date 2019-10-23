/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comics_table")
data class ComicEntity(
    @PrimaryKey var num: Int,
    var link: String,
    var news: String,
    @ColumnInfo(name = "image")
    var img: String,
    var title: String,
    @ColumnInfo(name = "alt_text")
    var alt: String,
    @ColumnInfo(name = "safe_title")
    var safeTitle: String,
    var transcript: String,
    var day: String,
    var month: String,
    var year: String,
    var isFavourite: Boolean = false
)
