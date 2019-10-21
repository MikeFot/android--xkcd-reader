/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.di.GlideApp
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.FavouriteImageView
import java.util.*

internal class FavouritesAdapter(private val context: Context) : PagedListAdapter<UiComicStrip, FavouritesAdapter.ViewHolder>(DiffCallback()),
    ListPreloader.PreloadModelProvider<UiComicStrip> {

    var comicActionListener: FavouritesActionListener? = null

    interface FavouritesActionListener {

        fun onImageClicked(uiComicStrip: UiComicStrip)

        fun onItemFavouriteChanged(id: Int, isFavourite: Boolean)
    }

    private val fullRequest = GlideApp.with(context)
        .asDrawable()
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .placeholder(ColorDrawable(Color.GRAY))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_favourite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comicStrip = getItem(position)
        if (comicStrip != null) {
            holder.run {
                titleTextView.text = comicStrip.title
                subtitleTextView.text = comicStrip.altText
                infoTextView.text = comicStrip.subtitle
                favouriteImageView.apply {
                    setFavourite(comicStrip.isFavourite)
                    setOnClickListener {
                        val isFavourite = !comicStrip.isFavourite
                        comicStrip.isFavourite = isFavourite
                        setFavourite(isFavourite)
                        comicActionListener?.onItemFavouriteChanged(
                            comicStrip.number,
                            isFavourite
                        )
                    }
                }
                fullRequest
                    .load(comicStrip.imageLink)
                    .into(holder.comicImageView)
                comicImageView.setOnClickListener {
                    comicActionListener?.onImageClicked(comicStrip)
                }
            }
        }
    }

    override fun getPreloadItems(position: Int): MutableList<UiComicStrip> {
        return currentList?.subList(position, position + 1) ?: Collections.emptyList()
    }

    override fun getPreloadRequestBuilder(item: UiComicStrip): RequestBuilder<Drawable> {
        return fullRequest.load(item.imageLink)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
