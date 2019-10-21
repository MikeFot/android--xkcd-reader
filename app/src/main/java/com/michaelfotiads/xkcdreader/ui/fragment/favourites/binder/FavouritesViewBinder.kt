package com.michaelfotiads.xkcdreader.ui.fragment.favourites.binder

import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.di.GlideApp
import com.michaelfotiads.xkcdreader.ui.base.BaseFragmentViewBinder
import com.michaelfotiads.xkcdreader.ui.base.BaseFragmentViewHolder
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter.FavouritesAdapter
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip

internal class FavouritesViewBinder(view: View) : BaseFragmentViewBinder<FavouritesViewBinder.ViewHolder>(view) {

    class ViewHolder(view: View) : BaseFragmentViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.favourites_recycler_view)
    }

    override val viewHolder = ViewHolder(view)

    private val favouritesAdapter: FavouritesAdapter by lazy {
        FavouritesAdapter(context)
    }

    fun initialiseAdapter() {
        favouritesAdapter.comicActionListener = object : FavouritesAdapter.FavouritesActionListener {
            override fun onImageClicked(uiComicStrip: UiComicStrip) {
            }

            override fun onItemFavouriteChanged(id: Int, isFavourite: Boolean) {
            }
        }
        viewHolder.recyclerView.adapter = favouritesAdapter
        val preloadSizeProvider = ViewPreloadSizeProvider<UiComicStrip>()
        val recyclerViewPreLoader = RecyclerViewPreloader<UiComicStrip>(GlideApp.with(viewHolder.rootView),
            favouritesAdapter, preloadSizeProvider, 10)
        viewHolder.recyclerView.addOnScrollListener(recyclerViewPreLoader)
    }

    fun setItems(pagedList: PagedList<UiComicStrip>) {
        favouritesAdapter.submitList(pagedList)


    }
}