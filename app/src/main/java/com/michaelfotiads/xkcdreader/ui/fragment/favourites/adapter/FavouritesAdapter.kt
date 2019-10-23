/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter

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
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.image.ImageLoader
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.FavouriteImageView

internal class FavouritesAdapter(
    private val imageHelper: ImageLoader
) : PagedListAdapter<UiComicStrip, FavouritesAdapter.ViewHolder>(DiffCallback()),
    ListPreloader.PreloadModelProvider<UiComicStrip> {

    var favouritesActionListener: FavouritesAdapterActionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_favourite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comicStrip = getItem(position)
        if (comicStrip != null) {
            bindItem(holder, comicStrip)
        }
    }

    private fun bindItem(holder: ViewHolder, comicStrip: UiComicStrip) {
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
                    favouritesActionListener?.onItemFavouriteToggled(
                        comicStrip.number,
                        isFavourite
                    )
                }
            }
            imageHelper.loadGlideIntoImageView(comicStrip.imageLink, holder.comicImageView)
            comicImageView.setOnClickListener { view ->
                favouritesActionListener?.onImageClicked(view, comicStrip.imageLink)
            }
        }
    }

    override fun getPreloadItems(position: Int): MutableList<UiComicStrip> {
        return currentList?.subList(position, position + 1) ?: mutableListOf()
    }

    override fun getPreloadRequestBuilder(item: UiComicStrip): RequestBuilder<Drawable> {
        return imageHelper.preLoadGlideImage(item.imageLink)
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
