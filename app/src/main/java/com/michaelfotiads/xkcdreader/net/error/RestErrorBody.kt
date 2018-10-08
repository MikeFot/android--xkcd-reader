/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.error

import com.google.gson.annotations.SerializedName

class RestErrorBody(
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<RestError?>?,
    @SerializedName("status_code") val statusCode: Int?
)
