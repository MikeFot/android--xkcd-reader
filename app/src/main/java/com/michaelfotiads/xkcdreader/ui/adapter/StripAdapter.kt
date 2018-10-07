/*
 * Developed by Michail Fotiadis on 07/10/18 18:40.
 * Last modified 07/10/18 18:40.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip

class StripAdapter(context: Context) : ArrayAdapter<UiComicStrip>(context, 0) {

    override fun getView(position: Int, contentView: View?, parent: ViewGroup): View {
        val holder: ViewHolder

        var taggedContentView = contentView

        if (taggedContentView == null) {
            val inflater = LayoutInflater.from(context)
            taggedContentView = inflater.inflate(R.layout.list_item_comic_strip, parent, false)
            holder = ViewHolder(taggedContentView)
            taggedContentView.tag = holder
        } else {
            holder = taggedContentView.tag as ViewHolder
        }

        getItem(position)?.let {
            holder.title.text = it.title
            holder.subtitle.text = it.altText
            Glide.with(context).load(it.imageLink).into(holder.image)
        }
        return taggedContentView!!
    }

    inner class ViewHolder(view: View) {
        val title: TextView = view.findViewById(R.id.title_text)
        var subtitle: TextView = view.findViewById(R.id.subtitle_text)
        var image: ImageView = view.findViewById(R.id.image_view)
    }
}