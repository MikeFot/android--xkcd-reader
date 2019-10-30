package com.michaelfotiads.xkcdreader.data.db.entity

import androidx.room.Embedded

data class CardPageWithComic(
    @Embedded var pageEntity: CardPageEntity,
    @Embedded var comicEntity: ComicEntity
)
