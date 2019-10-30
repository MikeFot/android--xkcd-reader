package com.michaelfotiads.xkcdreader.ui.fragment.comics.cards.adapter

import android.view.View

interface CardsAdapterActionListener {

    fun onImageClicked(view: View, imageLink: String)

    fun onItemFavouriteToggled(comicStripId: Int, isFavourite: Boolean)
}
