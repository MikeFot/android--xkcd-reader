/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.loader.error

/**
 */
enum class DataSourceErrorKind {
    COMMUNICATION,
    INTERNAL_SERVER_ERROR,
    NOT_PERMITTED,
    UNEXPECTED,
    REQUEST_FAILED,
    NO_NETWORK,
    ERROR_RETRIEVING_FROM_CACHE,
    ERROR_PERSISTING,
    IO_EXCEPTION,
    DESERIALIZATION_ERROR,
    INVALID_REQUEST_PARAMETERS,
    INVALID_CONTENT,
    NO_CONTENT_RETURNED,
    NOT_FOUND,
    AUTHENTICATION,
    PAYLOAD_TOO_LARGE,
    AUTHORISATION,
    BAD_REQUEST,
    EMAIL_ALREADY_USED,
    EMAIL_INVALID,
    EMAIL_MISSING,
    PASSWORD_INVALID,
}
