/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.net.error

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Modified from https://gist.github.com/koesie10/bc6c62520401cc7c858f#file-retrofitexception-java
 */
class RetrofitException internal constructor(
    message: String?,
    val url: String?,
    val response: Response<*>?,
    val kind: Kind,
    val code: Int?,
    exception: Throwable?,
    val retrofit: Retrofit?
) : RuntimeException(message, exception) {

    val deserialisedError: RestErrorBody?
        get() {
            return try {
                getErrorBodyAs(RestErrorBody::class.java)
            } catch (e: Exception) {
                null
            }
        }

    val errorBody: String?
        get() = if (response?.errorBody() == null) {
            null
        } else {
            getBodyAsString(response.errorBody())
        }

    /**
     * Identifies the event kind which triggered a [RetrofitException].
     */
    enum class Kind {
        NETWORK,
        HTTP,
        UNEXPECTED
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     */
    @Throws(IOException::class, IllegalStateException::class)
    fun <T> getErrorBodyAs(type: Class<T>): T? {

        return if (response?.errorBody() == null) {
            null
        } else {
            val converter = retrofit?.responseBodyConverter<T>(type, arrayOfNulls(0))
            converter?.convert(response.errorBody()!!)
        }
    }

    companion object {

        fun httpError(url: String, response: Response<*>, retrofit: Retrofit): RetrofitException {
            val message = "${response.code()} ${response.message()}"
            return RetrofitException(message, url, response, Kind.HTTP, response.code(), null, retrofit)
        }

        fun networkError(exception: IOException): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.NETWORK, null, exception, null)
        }

        fun unexpectedError(exception: Throwable): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, null, exception, null)
        }

        private fun getBodyAsString(responseBody: ResponseBody?): String? {
            return if (responseBody == null) {
                null
            } else {
                try {
                    responseBody.string()
                } catch (e: IOException) {
                    null
                } catch (e: IllegalStateException) {
                    null
                }
            }
        }
    }
}