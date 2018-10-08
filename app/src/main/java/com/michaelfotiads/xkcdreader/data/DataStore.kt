/*
 * Developed by Michail Fotiadis on 08/10/18 15:36.
 * Last modified 08/10/18 15:36.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.data

import android.content.Context
import com.chibatching.kotpref.KotprefModel

class DataStore(context: Context) : KotprefModel(context) {
    var currentStrip by intPref(default = 0)
    var maxStripIndex by intPref(default = 0)
}