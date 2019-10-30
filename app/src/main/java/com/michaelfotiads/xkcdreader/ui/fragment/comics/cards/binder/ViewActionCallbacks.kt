package com.michaelfotiads.xkcdreader.ui.fragment.comics.cards.binder

import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip

interface ViewActionCallbacks {
    fun onItemAppeared(uiComicStrip: UiComicStrip)
    fun onErrorShown()
    fun toggleFavourite(comicStripId: Int, isFavourite: Boolean)
}
