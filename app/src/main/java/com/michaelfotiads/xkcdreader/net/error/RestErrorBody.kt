/*
 * Developed by Michail Fotiadis on 07/10/18 18:03.
 * Last modified 07/10/18 18:03.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.net.error

import com.google.gson.annotations.SerializedName

class RestErrorBody(
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<RestError?>?,
    @SerializedName("status_code") val statusCode: Int?
)
