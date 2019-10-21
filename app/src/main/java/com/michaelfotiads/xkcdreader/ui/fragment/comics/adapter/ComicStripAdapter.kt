/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.FavouriteImageView

class ComicStripAdapter : PagedListAdapter<UiComicStrip, ComicStripAdapter.ViewHolder>(DiffCallback()) {

    var comicActionListener: ComicActionListener? = null

    interface ComicActionListener {

        fun onImageClicked(uiComicStrip: UiComicStrip)

        fun onItemFavouriteChanged(id: Int, isFavourite: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_comic_strip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comicStrip = getItem(position)
        if (comicStrip != null) {
            holder.titleTextView.text = comicStrip.title
            holder.subtitleTextView.text = comicStrip.altText
            holder.infoTextView.text = comicStrip.subtitle
            holder.favouriteImageView.apply {
                setFavourite(comicStrip.isFavourite)
                setOnClickListener {
                    comicStrip.isFavourite = !comicStrip.isFavourite
                    with(comicStrip.isFavourite) {
                        setFavourite(this)
                        comicActionListener?.onItemFavouriteChanged(
                            comicStrip.number,
                            this
                        )
                    }
                }
            }
            Glide.with(holder.comicImageView).asDrawable().load(comicStrip.imageLink).into(holder.comicImageView)
            holder.comicImageView.setOnClickListener {
                comicActionListener?.onImageClicked(comicStrip)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title_text)
        val subtitleTextView: TextView = view.findViewById(R.id.subtitle_text)
        val infoTextView: TextView = view.findViewById(R.id.info_text)
        val favouriteImageView: FavouriteImageView = view.findViewById(R.id.icon_favourite)
        val comicImageView: ImageView = view.findViewById(R.id.image_view)
    }

    class DiffCallback : DiffUtil.ItemCallback<UiComicStrip>() {
        override fun areItemsTheSame(oldItem: UiComicStrip, newItem: UiComicStrip): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: UiComicStrip, newItem: UiComicStrip): Boolean {
            return oldItem == newItem
        }
    }
}
