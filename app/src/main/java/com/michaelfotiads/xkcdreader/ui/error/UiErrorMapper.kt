/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.error

import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceError
import com.michaelfotiads.xkcdreader.net.loader.error.DataSourceErrorKind
import javax.inject.Inject

@Suppress("ComplexMethod")
class UiErrorMapper @Inject constructor() {

    fun convert(error: DataSourceError): UiError {
        return when (error.kind) {
            DataSourceErrorKind.COMMUNICATION -> UiError.COMMUNICATION
            DataSourceErrorKind.INTERNAL_SERVER_ERROR -> UiError.INTERNAL_SERVER_ERROR
            DataSourceErrorKind.NOT_PERMITTED -> UiError.NOT_PERMITTED
            DataSourceErrorKind.UNEXPECTED -> UiError.UNEXPECTED
            DataSourceErrorKind.REQUEST_FAILED -> UiError.REQUEST_FAILED
            DataSourceErrorKind.NO_NETWORK -> UiError.NO_NETWORK
            DataSourceErrorKind.ERROR_RETRIEVING_FROM_CACHE -> UiError.UNEXPECTED
            DataSourceErrorKind.ERROR_PERSISTING -> UiError.UNEXPECTED
            DataSourceErrorKind.IO_EXCEPTION -> UiError.UNEXPECTED
            DataSourceErrorKind.DESERIALIZATION_ERROR -> UiError.UNEXPECTED
            DataSourceErrorKind.INVALID_REQUEST_PARAMETERS -> UiError.INVALID_REQUEST_PARAMETERS
            DataSourceErrorKind.INVALID_CONTENT -> UiError.BAD_REQUEST
            DataSourceErrorKind.NO_CONTENT_RETURNED -> UiError.BAD_REQUEST
            DataSourceErrorKind.NOT_FOUND -> UiError.NOT_FOUND
            DataSourceErrorKind.AUTHENTICATION -> UiError.AUTHENTICATION
            DataSourceErrorKind.AUTHORISATION -> UiError.AUTHORISATION
            DataSourceErrorKind.BAD_REQUEST -> UiError.BAD_REQUEST
            DataSourceErrorKind.EMAIL_ALREADY_USED -> UiError.INVALID_REQUEST_PARAMETERS
            DataSourceErrorKind.EMAIL_INVALID -> UiError.INVALID_REQUEST_PARAMETERS
            DataSourceErrorKind.EMAIL_MISSING -> UiError.INVALID_REQUEST_PARAMETERS
            DataSourceErrorKind.PASSWORD_INVALID -> UiError.INVALID_REQUEST_PARAMETERS
            DataSourceErrorKind.PAYLOAD_TOO_LARGE -> UiError.PAYLOAD_TOO_LARGE
        }
    }
}
