package com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter

import android.view.View

interface FavouritesAdapterActionListener {

    fun onImageClicked(view: View, imageLink: String)

    fun onItemFavouriteToggled(comicStripId: Int, isFavourite: Boolean)
}
