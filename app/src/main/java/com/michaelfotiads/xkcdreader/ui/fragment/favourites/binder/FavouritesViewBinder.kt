package com.michaelfotiads.xkcdreader.ui.fragment.favourites.binder

import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter.FavouritesAdapter
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter.FavouritesAdapterActionListener
import com.michaelfotiads.xkcdreader.ui.image.ImageLoader
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.base.BaseFragmentViewBinder
import com.michaelfotiads.xkcdreader.ui.view.base.BaseFragmentViewHolder

internal class FavouritesViewBinder(
    view: View,
    private val imageHelper: ImageLoader
) : BaseFragmentViewBinder<FavouritesViewBinder.ViewHolder>(view) {

    class ViewHolder(view: View) : BaseFragmentViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.favourites_recycler_view)
    }

    override val viewHolder = ViewHolder(view)

    private val favouritesAdapter: FavouritesAdapter by lazy {
        FavouritesAdapter(imageHelper)
    }

    var callbacks: ViewActionCallbacks? = null

    fun initialiseAdapter() {
        favouritesAdapter.favouritesActionListener = object : FavouritesAdapterActionListener {
            override fun onImageClicked(view: View, imageLink: String) {
                imageHelper.showFrescoImage(view, imageLink, R.dimen.margin_16dp)
            }

            override fun onItemFavouriteToggled(comicStripId: Int, isFavourite: Boolean) {
                callbacks?.toggleFavourite(comicStripId, isFavourite)
            }
        }
        viewHolder.recyclerView.adapter = favouritesAdapter
        viewHolder.recyclerView.addOnScrollListener(
            imageHelper.generateRecyclerViewPreLoader(
                viewHolder.rootView,
                favouritesAdapter,
                10)
        )
    }

    fun setItems(pagedList: PagedList<UiComicStrip>) {
        favouritesAdapter.submitList(pagedList)
    }
}
