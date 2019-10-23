/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UiComicStrip(
    val number: Int,
    val imageLink: String,
    val shareLink: String,
    val title: String,
    val altText: String,
    val subtitle: String,
    var isFavourite: Boolean
) : Parcelable
