/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader

import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceError

class RepoResult<T>(
    val payload: T? = null,
    val dataSourceError: DataSourceError? = null,
    val next: String? = null
)
