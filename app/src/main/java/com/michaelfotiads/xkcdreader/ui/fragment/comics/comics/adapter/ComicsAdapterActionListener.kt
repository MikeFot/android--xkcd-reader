package com.michaelfotiads.xkcdreader.ui.fragment.comics.comics.adapter

import android.view.View

interface ComicsAdapterActionListener {

    fun onImageClicked(view: View, imageLink: String)

    fun onItemFavouriteToggled(comicStripId: Int, isFavourite: Boolean)
}
