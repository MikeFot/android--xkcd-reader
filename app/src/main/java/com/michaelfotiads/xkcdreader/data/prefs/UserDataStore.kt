/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data.prefs

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class UserDataStore(context: Context) : KotprefModel(context) {
    var maxStripIndex by intPref(default = 0)
}
