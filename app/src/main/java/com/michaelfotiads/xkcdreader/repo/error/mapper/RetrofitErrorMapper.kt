/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.repo.error.mapper

import com.michaelfotiads.xkcdreader.net.error.RestError
import com.michaelfotiads.xkcdreader.net.error.RestErrorBody
import com.michaelfotiads.xkcdreader.net.error.RetrofitException
import com.michaelfotiads.xkcdreader.repo.error.DataSourceError
import com.michaelfotiads.xkcdreader.repo.error.DataSourceErrorKind
import com.michaelfotiads.xkcdreader.repo.error.ErrorMapper
import javax.inject.Inject

class RetrofitErrorMapper @Inject constructor() : ErrorMapper<Throwable> {

    @Suppress("ComplexMethod", "NestedBlockDepth")
    override fun convert(
        error: Throwable?
    ): DataSourceError {

        val dataSourceError: DataSourceError
        val warnings = HashMap<String, String>()
        val errorMessage: String?

        when (error) {
            null -> dataSourceError = DataSourceError(
                DataSourceErrorKind.UNEXPECTED.toString(), DataSourceErrorKind.UNEXPECTED
            )
            !is RetrofitException -> dataSourceError =
                DataSourceError(error.message, DataSourceErrorKind.UNEXPECTED)
            else -> {
                val retrofitException: RetrofitException = error

                val restErrorBody: RestErrorBody? = retrofitException.deserialisedError
                if (restErrorBody != null) {
                    errorMessage = when {
                        !restErrorBody.message.isNullOrEmpty() -> restErrorBody.message
                        else -> retrofitException.message
                    }
                    if (restErrorBody.errors != null && restErrorBody.errors.isNotEmpty()) {
                        val restWarnings = restErrorBody.errors.filterNotNull()
                        restWarnings.forEach { restError: RestError ->
                            if (restError.key != null && restError.message != null) {
                                val key: String = restError.key
                                val message: String = restError.message
                                warnings[key] = message
                            }
                        }
                    }
                } else {
                    errorMessage = retrofitException.message
                }

                val kind: DataSourceErrorKind =
                    getErrorKindFromException(retrofitException, warnings)
                dataSourceError = DataSourceError(errorMessage, kind)
            }
        }

        warnings.forEach { dataSourceError.addWarning(it.key, it.value) }

        return dataSourceError
    }

    @Suppress("ComplexMethod")
    private fun getErrorKindFromException(
        retrofitException: RetrofitException,
        @Suppress("UNUSED_PARAMETER")
        warnings: HashMap<String, String>
    ): DataSourceErrorKind {
        return when (retrofitException.kind) {
            RetrofitException.Kind.HTTP -> {
                val code = retrofitException.code
                when (code) {
                    null -> DataSourceErrorKind.UNEXPECTED
                    CODE_400 -> DataSourceErrorKind.BAD_REQUEST
                    CODE_401 -> DataSourceErrorKind.AUTHORISATION
                    CODE_403 -> DataSourceErrorKind.NOT_PERMITTED
                    CODE_404 -> DataSourceErrorKind.NOT_FOUND
                    CODE_423 -> DataSourceErrorKind.AUTHENTICATION
                    CODE_413 -> DataSourceErrorKind.PAYLOAD_TOO_LARGE
                    in CODE_400 until CODE_500 -> DataSourceErrorKind.REQUEST_FAILED
                    in CODE_300 until CODE_400 -> DataSourceErrorKind.NOT_FOUND
                    in CODE_500 until CODE_600 -> DataSourceErrorKind.INTERNAL_SERVER_ERROR
                    else -> DataSourceErrorKind.UNEXPECTED
                }
            }
            RetrofitException.Kind.NETWORK -> DataSourceErrorKind.COMMUNICATION
            RetrofitException.Kind.UNEXPECTED -> DataSourceErrorKind.UNEXPECTED
        }
    }

    companion object {
        private const val CODE_300 = 300
        private const val CODE_400 = 400
        private const val CODE_401 = 401
        private const val CODE_403 = 403
        private const val CODE_404 = 404
        private const val CODE_413 = 413
        private const val CODE_423 = 423
        private const val CODE_500 = 500
        private const val CODE_600 = 600
    }
}
