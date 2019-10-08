/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2019.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.adapter.ComicStripAdapter
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModel
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModelFactory
import com.stfalcon.frescoimageviewer.ImageViewer
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import dagger.android.support.AndroidSupportInjection
import es.dmoral.toasty.Toasty
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_comics.*
import timber.log.Timber

class ComicsFragment : Fragment() {

    companion object {
        private const val INDEX_PROGRESS = 0
        private const val INDEX_CONTENT = 1

        @JvmStatic
        fun newInstance() = ComicsFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }
    private val adapter: ComicStripAdapter by lazy {
        ComicStripAdapter(requireContext())
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel.resultsData.observe(this, Observer(::onResult))
        viewModel.errorData.observe(this, Observer(::onError))
        viewModel.resetAdapterData.observe(this, Observer(::onResetAdapter))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseCardAdapter()
        viewModel.loadInitialData()
    }

    fun getTopItem(): UiComicStrip? {
        return when {
            adapter.count > 0 -> adapter.getItem(0)
            else -> null
        }
    }

    private fun initialiseCardAdapter() {
        adapter.comicActionListener = object : ComicStripAdapter.ComicActionListener {
            override fun onItemFavouriteChanged(id: Int, isFavourite: Boolean) {
                viewModel.toggleFavourite(id, isFavourite)
            }
        }
        card_stack_view.setAdapter(adapter)
        card_stack_view.setCardEventListener(object : CardStackView.CardEventListener {
            override fun onCardDragging(percentX: Float, percentY: Float) {
                // NOOP
            }

            override fun onCardReversed() {
                Timber.d("On Card Reversed")
            }

            override fun onCardMovedToOrigin() {
                Timber.d("On Card Moved to Origin")
            }

            override fun onCardClicked(index: Int) {
                Timber.d("On Card Clicked Index $index")
                adapter.getItem(index)?.let(::onItemClicked)
            }

            override fun onCardSwiped(direction: SwipeDirection?) {
                onItemSwiped(direction)
            }
        })
    }

    private fun onItemSwiped(direction: SwipeDirection?) {
        synchronized(this) {
            Timber.d("On Card Swiped $direction and we are now at top index ${card_stack_view.topIndex}")
            // remove the item so it won't refresh with the adapter
            adapter.remove(adapter.getItem(card_stack_view.topIndex - 1))
            if (adapter.count == 0) {
                Toasty.success(requireActivity(), getString(R.string.message_all_caught_up)).show()
            } else {
                // remove the item so it won't refresh with the adapter
                viewModel.decrementCurrentStrip()
                viewModel.loadAdditionalData(adapter.count)
            }
        }
    }

    private fun onItemClicked(item: UiComicStrip) {
        ImageViewer.Builder<String>(requireActivity(), listOf(item.imageLink))
            .hideStatusBar(false)
            .setImageMargin(activity, R.dimen.margin_16dp)
            .show()
    }

    private fun onResetAdapter(shouldReset: Boolean?) {
        if (shouldReset == true) {
            Timber.d("Resetting adapter data")
            adapter.clear()
            adapter.notifyDataSetChanged()
            view_flipper.displayedChild = INDEX_PROGRESS
        }
    }

    private fun onResult(comicStrip: UiComicStrip) {
        Timber.d("Loaded item with ID ${comicStrip.number}")
        view_flipper.displayedChild = INDEX_CONTENT
        adapter.add(comicStrip)
        Timber.d("Adapter has ${adapter.count} items")
        viewModel.loadAdditionalData(adapter.count)
    }

    private fun onError(uiError: UiError?) {
        uiError?.let {
            view_flipper.displayedChild = INDEX_CONTENT
            Toasty.error(requireActivity(), getString(uiError.messageResId)).show()
            viewModel.clearError()
        }
    }
}
