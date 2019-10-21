package com.michaelfotiads.xkcdreader.ui.fragment.comics.binder

import android.view.View
import android.widget.ViewFlipper
import androidx.paging.PagedList
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.base.BaseFragmentViewBinder
import com.michaelfotiads.xkcdreader.ui.base.BaseFragmentViewHolder
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.fragment.comics.adapter.ComicStripAdapter
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.stfalcon.frescoimageviewer.ImageViewer
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import es.dmoral.toasty.Toasty


private const val TRANSLATION_INTERVAL = 8.0f
private const val INDEX_PROGRESS = 0
private const val INDEX_CONTENT = 1

internal class ViewBinder(view: View) : BaseFragmentViewBinder<ViewBinder.ViewHolder>(view) {

    class ViewHolder(view: View) : BaseFragmentViewHolder(view) {
        val viewFlipper: ViewFlipper = view.findViewById(R.id.view_flipper)
        val cardStackView: CardStackView = view.findViewById(R.id.card_stack_view)
    }

    interface ViewActionCallbacks {
        fun onItemAppeared(uiComicStrip: UiComicStrip)
        fun onErrorShown()
        fun toggleFavourite(id: Int, favourite: Boolean)
    }

    override val viewHolder = ViewHolder(view)

    private val comicStripAdapter: ComicStripAdapter by lazy {
        ComicStripAdapter()
    }
    private val comicStripListener: CardStackListener by lazy {
        ComicsCardStackListener()
    }
    private val comicStripLayoutManager by lazy {
        CardStackLayoutManager(context, comicStripListener).apply {
            setStackFrom(StackFrom.Top)
            setTranslationInterval(TRANSLATION_INTERVAL)
        }
    }

    var callbacks: ViewActionCallbacks? = null

    inner class ComicsCardStackListener : CardStackListener {
        override fun onCardDisappeared(view: View?, position: Int) {
            val item = comicStripAdapter.currentList?.get(position)
            if (item?.number == 1) {
                Toasty.info(context, R.string.message_all_caught_up).show()
            }
        }

        override fun onCardDragging(direction: Direction?, ratio: Float) {}

        override fun onCardSwiped(direction: Direction?) {}

        override fun onCardCanceled() {}

        override fun onCardAppeared(view: View?, position: Int) {
            comicStripAdapter.currentList?.get(position)?.let { comicStrip ->
                callbacks?.onItemAppeared(comicStrip)
            }
        }

        override fun onCardRewound() {}
    }

    fun initialiseCardAdapter() {
        showProgress()
        comicStripAdapter.comicActionListener = object : ComicStripAdapter.ComicActionListener {
            override fun onImageClicked(uiComicStrip: UiComicStrip) {
                onItemClicked(uiComicStrip)
            }

            override fun onItemFavouriteChanged(id: Int, isFavourite: Boolean) {
                callbacks?.toggleFavourite(id, isFavourite)
            }
        }
        viewHolder.cardStackView.apply {
            adapter = comicStripAdapter
            layoutManager = comicStripLayoutManager
        }
    }

    private fun onItemClicked(item: UiComicStrip) {
        ImageViewer.Builder<String>(context, listOf(item.imageLink))
            .hideStatusBar(false)
            .setImageMargin(context, R.dimen.margin_16dp)
            .show()
    }

    fun setAdapterContent(pagedList: PagedList<UiComicStrip>) {
        showContent()
        comicStripAdapter.submitList(pagedList)
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

    fun showGettingLatest() {
        Toasty.info(context, context.getString(R.string.message_reset_data)).show()
    }

    fun showPageNotFound() {
        Toasty.error(
            context,
            context.getString(R.string.message_page_not_found)
        ).show()
    }

}