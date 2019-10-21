package com.michaelfotiads.xkcdreader.data.db.entity

import androidx.room.Embedded

data class PageWithComic(
    @Embedded var pageEntity: PageEntity,
    @Embedded var comicEntity: ComicEntity
)
