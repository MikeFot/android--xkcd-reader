/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.api.model

import com.google.gson.annotations.SerializedName

/**
 * @see <a href="https://any-api.com/xkcd_com/xkcd_com/docs/Definitions/comic">Any API definition</a>
 *
 * {
 * "month": "10",
 * "num": 2055,
 * "link": "",
 * "year": "2018",
 * "news": "",
 * "safe_title": "Bluetooth",
 * "transcript": "",
 * "alt": "Bluetooth is actually named for the tenth-century Viking king Harald \"Bluetooth\" Gormsson, but the protocol developed by Harald was a wireless charging standard unrelated to the modern Bluetooth except by name.",
 * "img": "https://imgs.xkcd.com/comics/bluetooth.png",
 * "title": "Bluetooth",
 * "day": "5"
 * }
 *
 */
data class ComicStrip(
    @SerializedName("month") val month: String,
    @SerializedName("num") val num: Int,
    @SerializedName("link") val link: String,
    @SerializedName("year") val year: String,
    @SerializedName("news") val news: String,
    @SerializedName("safe_title") val safeTitle: String,
    @SerializedName("transcript") val transcript: String,
    @SerializedName("alt") val alt: String,
    @SerializedName("img") val img: String,
    @SerializedName("title") val title: String,
    @SerializedName("day") val day: String
)
