package com.michaelfotiads.xkcdreader.ui.fragment.comics.model

import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip

sealed class ComicAction {
    object Idle : ComicAction()
    object ShowContent : ComicAction()
    data class ShowError(val uiError: UiError) : ComicAction()
    data class ShowDialog(val appDialog: AppDialog) : ComicAction()
    data class Share(val uiComicStrip: UiComicStrip) : ComicAction()
}
