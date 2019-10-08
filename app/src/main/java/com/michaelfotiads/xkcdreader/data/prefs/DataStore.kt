/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data.prefs

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class DataStore(context: Context) : KotprefModel(context) {
    var currentStrip by intPref(default = 0)
    var maxStripIndex by intPref(default = 0)
}
