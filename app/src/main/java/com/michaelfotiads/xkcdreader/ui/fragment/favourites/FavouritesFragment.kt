/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2019.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.favourites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.michaelfotiads.xkcdreader.R
import dagger.android.support.AndroidSupportInjection

class FavouritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavouritesFragment()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }
}
