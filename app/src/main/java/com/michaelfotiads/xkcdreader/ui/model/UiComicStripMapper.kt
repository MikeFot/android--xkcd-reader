/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.model

import android.content.res.Resources
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.data.db.entity.ComicEntity
import javax.inject.Inject

class UiComicStripMapper @Inject constructor(private val resources: Resources) {

    companion object {
        private const val PADDING_START_LENGTH = 2
        private const val PADDING_CHAR = '0'
    }

    fun convert(comicEntities: List<ComicEntity>): List<UiComicStrip> {

        val comicStrips = mutableListOf<UiComicStrip>()
        comicEntities.forEach {
            comicStrips.add(convert(it))
        }
        return comicStrips
    }

    fun convert(comicEntity: ComicEntity): UiComicStrip {

        val day = comicEntity.day.padStart(PADDING_START_LENGTH, PADDING_CHAR)
        val month = comicEntity.month.padStart(PADDING_START_LENGTH, PADDING_CHAR)
        val year = comicEntity.year

        val displayDate = resources.getString(R.string.comic_strip_display_date, day, month, year)
        val webLink = resources.getString(R.string.comic_strip_web_link, comicEntity.num)
        val subtitle = resources.getString(
            R.string.comic_strip_info,
            comicEntity.num.toString(),
            displayDate,
            webLink
        )

        return UiComicStrip(
            number = comicEntity.num,
            imageLink = comicEntity.img,
            shareLink = webLink,
            title = comicEntity.title,
            altText = comicEntity.alt,
            subtitle = subtitle,
            isFavourite = comicEntity.isFavourite
        )
    }
}
