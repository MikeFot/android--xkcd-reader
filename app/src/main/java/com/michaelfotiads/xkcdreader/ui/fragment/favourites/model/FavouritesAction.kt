package com.michaelfotiads.xkcdreader.ui.fragment.favourites.model

import com.michaelfotiads.xkcdreader.ui.model.AppDialog

sealed class FavouritesAction {
    object Idle : FavouritesAction()
    data class ShowDialog(val appDialog: AppDialog) : FavouritesAction()
}
