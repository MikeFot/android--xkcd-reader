/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.model

sealed class AppDialog {
    data class Search(val id: Int) : AppDialog()
    object About : AppDialog()
}
