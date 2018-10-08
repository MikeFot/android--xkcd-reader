/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader.error

data class DataSourceError(val errorMessage: String?, val kind: DataSourceErrorKind) {
    val creationTime: Long
    val warnings: MutableMap<String, String>

    init {
        this.warnings = HashMap()
        this.creationTime = System.currentTimeMillis()
    }

    fun addWarning(key: String, value: String) {
        warnings[key] = value
    }
}
