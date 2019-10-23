/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.error

import com.google.gson.annotations.SerializedName

class RestError(
    @SerializedName("key") val key: String?,
    @SerializedName("message") val message: String?
)
