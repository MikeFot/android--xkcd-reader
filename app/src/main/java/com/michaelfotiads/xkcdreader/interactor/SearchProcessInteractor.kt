package com.michaelfotiads.xkcdreader.interactor

import com.michaelfotiads.xkcdreader.data.prefs.UserDataStore
import javax.inject.Inject

class SearchProcessInteractor @Inject constructor(
    private val userDataStore: UserDataStore
) {

    fun process(query: String?): Int? {
        return if (query.isNullOrBlank()) {
            null
        } else {
            try {
                val stripNumber = query.toInt()
                if (stripNumber in 1..userDataStore.maxStripIndex) {
                    stripNumber
                } else {
                    null
                }
            } catch (exception: NumberFormatException) {
                null
            }
        }
    }
}
