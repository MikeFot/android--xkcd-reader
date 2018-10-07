/*
 * Developed by Michail Fotiadis on 07/10/18 18:21.
 * Last modified 07/10/18 18:21.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.ui.model

import com.michaelfotiads.xkcdreader.net.api.model.ComicStrip

class UiComicStripMapper {

    fun convert(comicStrip: ComicStrip): UiComicStrip {

        return UiComicStrip(number = comicStrip.num,
                            imageLink = comicStrip.img,
                            title = comicStrip.title,
                            altText = comicStrip.alt)
    }
}