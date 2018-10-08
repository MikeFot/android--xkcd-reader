/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.model

import com.michaelfotiads.xkcdreader.net.api.model.ComicStrip

class UiComicStripMapper {

    fun convert(comicStrip: ComicStrip): UiComicStrip {

        val day = comicStrip.day.padStart(2, '0')
        val month = comicStrip.month.padStart(2, '0')
        val year = comicStrip.year

        val displayDate = "$day/$month/$year"

        return UiComicStrip(number = comicStrip.num,
                            imageLink = comicStrip.img,
                            title = comicStrip.title,
                            altText = comicStrip.alt,
                            displayDate = displayDate)
    }
}