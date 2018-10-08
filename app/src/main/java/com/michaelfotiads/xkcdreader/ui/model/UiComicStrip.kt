/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.model

import android.os.Parcel
import android.os.Parcelable

data class UiComicStrip(
        val number: Int,
        val imageLink: String,
        val title: String,
        val altText: String,
        val displayDate: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(number)
        parcel.writeString(imageLink)
        parcel.writeString(title)
        parcel.writeString(altText)
        parcel.writeString(displayDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UiComicStrip> {
        override fun createFromParcel(parcel: Parcel): UiComicStrip {
            return UiComicStrip(parcel)
        }

        override fun newArray(size: Int): Array<UiComicStrip?> {
            return arrayOfNulls(size)
        }
    }
}