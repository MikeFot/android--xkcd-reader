/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader.error

interface ErrorMapper<in T> {
    fun convert(error: T?): DataSourceError
}
