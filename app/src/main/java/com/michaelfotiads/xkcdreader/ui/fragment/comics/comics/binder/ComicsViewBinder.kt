package com.michaelfotiads.xkcdreader.ui.fragment.comics.comics.binder

import android.view.View
import android.widget.ViewFlipper
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.fragment.comics.comics.adapter.ComicsAdapter
import com.michaelfotiads.xkcdreader.ui.fragment.comics.comics.adapter.ComicsAdapterActionListener
import com.michaelfotiads.xkcdreader.ui.image.ImageLoader
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.base.BaseFragmentViewBinder
import com.michaelfotiads.xkcdreader.ui.view.base.BaseFragmentViewHolder
import es.dmoral.toasty.Toasty

private const val INDEX_PROGRESS = 0
private const val INDEX_CONTENT = 1
private const val ITEMS_TO_PRELOAD = 10

internal class ComicsViewBinder(
    view: View,
    private val imageHelper: ImageLoader
) : BaseFragmentViewBinder<ComicsViewBinder.ViewHolder>(view) {

    class ViewHolder(view: View) : BaseFragmentViewHolder(view) {
        val viewFlipper: ViewFlipper = view.findViewById(R.id.comics_view_flipper)
        val recyclerView: RecyclerView = view.findViewById(R.id.comics_recycler_view)
    }

    override val viewHolder = ViewHolder(view)

    private val comicStripAdapter: ComicsAdapter by lazy {
        ComicsAdapter(imageHelper)
    }

    var callbacks: ViewActionCallbacks? = null

    fun initialiseRecyclerAdapter() {
        showProgress()
        comicStripAdapter.actionListener = object : ComicsAdapterActionListener {
            override fun onImageClicked(view: View, imageLink: String) {
                imageHelper.showFrescoImage(view, imageLink, R.dimen.margin_16dp)
            }

            override fun onItemFavouriteToggled(comicStripId: Int, isFavourite: Boolean) {
                callbacks?.toggleFavourite(comicStripId, isFavourite)
            }
        }
        viewHolder.recyclerView.apply {
            adapter = comicStripAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)
            addOnScrollListener(
                imageHelper.generateRecyclerViewPreLoader(
                    viewHolder.rootView,
                    comicStripAdapter,
                    ITEMS_TO_PRELOAD)
            )
            LinearSnapHelper().attachToRecyclerView(this)
        }
    }

    fun setAdapterContent(pagedList: PagedList<UiComicStrip>) {
        showContent()
        comicStripAdapter.submitList(pagedList)
    }

    fun resetPosition() {
        viewHolder.recyclerView.post {
            viewHolder.recyclerView.scrollToPosition(0)
        }
    }

    fun showContent() {
        viewHolder.viewFlipper.displayedChild = INDEX_CONTENT
    }

    private fun showProgress() {
        viewHolder.viewFlipper.displayedChild = INDEX_PROGRESS
    }

    fun onError(uiError: UiError?) {
        if (uiError != null) {
            showContent()
            Toasty.error(context, context.getString(uiError.messageResId)).show()
            callbacks?.onErrorShown()
        }
    }
}
