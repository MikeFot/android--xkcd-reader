package com.michaelfotiads.xkcdreader.ui.fragment.comics.model

import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.model.AppDialog

sealed class ComicAction {
    object Idle : ComicAction()
    object ShowContent : ComicAction()
    data class ShowError(val uiError: UiError) : ComicAction()
    data class ShowDialog(val appDialog: AppDialog) : ComicAction()
}