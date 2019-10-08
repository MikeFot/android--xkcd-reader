/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
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
import com.michaelfotiads.xkcdreader.ui.view.FavouriteImageView

class ComicStripAdapter(context: Context) : ArrayAdapter<UiComicStrip>(context, 0) {

    interface ComicActionListener {
        fun onItemFavouriteChanged(id: Int, isFavourite: Boolean)
    }

    var comicActionListener: ComicActionListener? = null

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

        getItem(position)?.let { bindView(it, holder) }
        return taggedContentView!!
    }

    private fun bindView(comicStrip: UiComicStrip, holder: ViewHolder) {
        holder.titleTextView.text = comicStrip.title
        holder.subtitleTextView.text = comicStrip.altText
        holder.infoTextView.text = comicStrip.subtitle
        holder.favouriteImageView.apply {
            setFavourite(comicStrip.isFavourite)
            setOnClickListener {
                comicStrip.isFavourite = !comicStrip.isFavourite
                with(comicStrip.isFavourite) {
                    setFavourite(this)
                    notifyDataSetChanged()
                    comicActionListener?.onItemFavouriteChanged(
                        comicStrip.number,
                        this
                    )
                }
            }
        }
        Glide.with(context).load(comicStrip.imageLink).into(holder.comicImageView)
    }

    inner class ViewHolder(view: View) {
        val titleTextView = view.findViewById<TextView>(R.id.title_text)!!
        val subtitleTextView = view.findViewById<TextView>(R.id.subtitle_text)!!
        val infoTextView = view.findViewById<TextView>(R.id.info_text)!!
        val favouriteImageView = view.findViewById<FavouriteImageView>(R.id.icon_favourite)!!
        val comicImageView = view.findViewById<ImageView>(R.id.image_view)!!
    }
}
