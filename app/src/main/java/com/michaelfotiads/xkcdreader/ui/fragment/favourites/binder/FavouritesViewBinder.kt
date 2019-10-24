package com.michaelfotiads.xkcdreader.ui.fragment.favourites.binder

import android.view.View
import android.widget.ViewFlipper
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter.FavouritesAdapter
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.adapter.FavouritesAdapterActionListener
import com.michaelfotiads.xkcdreader.ui.image.ImageLoader
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.base.BaseFragmentViewBinder
import com.michaelfotiads.xkcdreader.ui.view.base.BaseFragmentViewHolder

private const val INDEX_EMPTY = 0
private const val INDEX_CONTENT = 1
private const val ITEMS_TO_PRELOAD = 10

internal class FavouritesViewBinder(
    view: View,
    private val imageHelper: ImageLoader
) : BaseFragmentViewBinder<FavouritesViewBinder.ViewHolder>(view) {

    class ViewHolder(view: View) : BaseFragmentViewHolder(view) {
        val viewFlipper: ViewFlipper = view.findViewById(R.id.favourites_flipper)
        val recyclerView: RecyclerView = view.findViewById(R.id.favourites_recycler_view)
    }

    override val viewHolder = ViewHolder(view)

    private val favouritesAdapter: FavouritesAdapter by lazy {
        FavouritesAdapter(imageHelper)
    }

    var callbacks: ViewActionCallbacks? = null

    fun initialiseAdapter() {

        viewHolder.viewFlipper.displayedChild = INDEX_EMPTY

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
                ITEMS_TO_PRELOAD)
        )
    }

    fun setItems(pagedList: PagedList<UiComicStrip>) {
        favouritesAdapter.submitList(pagedList)
        if (pagedList.isNotEmpty()) {
            viewHolder.viewFlipper.displayedChild = INDEX_CONTENT
        }
    }
}
