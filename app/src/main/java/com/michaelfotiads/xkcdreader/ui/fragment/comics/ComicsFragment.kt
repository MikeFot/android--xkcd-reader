/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2019.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.dialog.DialogFactory
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.fragment.comics.adapter.ComicStripAdapter
import com.michaelfotiads.xkcdreader.ui.fragment.comics.model.ComicAction
import com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel.ComicsViewModel
import com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel.ComicsViewModelFactory
import com.michaelfotiads.xkcdreader.ui.intent.IntentDispatcher
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.stfalcon.frescoimageviewer.ImageViewer
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import dagger.android.support.AndroidSupportInjection
import es.dmoral.toasty.Toasty
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_comics.*

internal class ComicsFragment : Fragment() {

    companion object {
        private const val INDEX_PROGRESS = 0
        private const val INDEX_CONTENT = 1

        @JvmStatic
        fun newInstance() = ComicsFragment()
    }

    @Inject
    lateinit var viewModelFactory: ComicsViewModelFactory
    @Inject
    lateinit var intentDispatcher: IntentDispatcher
    @Inject
    lateinit var dialogFactory: DialogFactory

    private val viewModel: ComicsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ComicsViewModel::class.java)
    }
    private val comicStripAdapter: ComicStripAdapter by lazy {
        ComicStripAdapter()
    }
    private val comicStripLayoutManager by lazy {
        CardStackLayoutManager(requireContext(), comicStripListener).apply {
            setStackFrom(StackFrom.Top)
            setTranslationInterval(8.0f)
        }
    }

    private val comicStripListener: CardStackListener by lazy {
        object : CardStackListener {
            override fun onCardDisappeared(view: View?, position: Int) {
                val item = comicStripAdapter.currentList?.get(position)
                if (item?.number == 1) {
                    Toasty.info(requireContext(), R.string.message_all_caught_up).show()
                }
            }

            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {}

            override fun onCardCanceled() {}

            override fun onCardAppeared(view: View?, position: Int) {
                viewModel.setCurrentItem(comicStripAdapter.currentList?.get(position))
            }

            override fun onCardRewound() {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val tintColor = ContextCompat.getColor(requireActivity(), R.color.white)
        val colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
        for (i in 0 until menu.size()) {
            menu.getItem(i).icon?.run {
                mutate()
                setColorFilter(colorFilter)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_refresh -> {
                resetAndLoadData()
                true
            }
            R.id.menu_item_search -> {
                viewModel.showSearch()
                true
            }
            R.id.menu_item_about -> {
                viewModel.showAboutDialog()
                true
            }
            R.id.menu_item_share -> {
                viewModel.shareCurrentItem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseCardAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.pagedItems.observe(viewLifecycleOwner, Observer(this@ComicsFragment::setAdapterContent))
        viewModel.actionLiveData.observe(viewLifecycleOwner, Observer(this@ComicsFragment::processAction))
        viewModel.lastLoadedIndex.observe(viewLifecycleOwner, Observer { lastIndex ->
            viewModel.loadComic(lastIndex ?: 0)
        })
    }

    private fun setAdapterContent(pagedList: PagedList<UiComicStrip>) {
        view_flipper.displayedChild = INDEX_CONTENT
        comicStripAdapter.submitList(pagedList)
    }

    private fun processAction(action: ComicAction) {
        when (action) {
            is ComicAction.ShowContent -> view_flipper.displayedChild = INDEX_CONTENT
            is ComicAction.ShowError -> onError(action.uiError)
            is ComicAction.ShowDialog -> showDialog(action.appDialog)
            is ComicAction.Share -> intentDispatcher.share(requireActivity(), action.uiComicStrip)
            is ComicAction.Idle -> {
                // NOOP
            }
        }
    }

    private fun showDialog(appDialog: AppDialog?) {
        when (appDialog) {
            is AppDialog.Search -> dialogFactory.showSearch(
                requireActivity(), appDialog.id, this@ComicsFragment::processSearchQuery)
            is AppDialog.About -> dialogFactory.showAboutDialog(requireActivity())
        }
    }

    private fun resetAndLoadData() {
        Toasty.info(requireContext(), getString(R.string.message_reset_data)).show()
        viewModel.refresh()
    }

    private fun processSearchQuery(query: String?, maxStripIndex: Int) {
        if (!query.isNullOrBlank()) {
            val stripNumber = query.toInt()
            if (stripNumber in 1..maxStripIndex) {
                viewModel.setSearchParameter(stripNumber)
            } else {
                Toasty.error(
                    requireContext(),
                    getString(R.string.message_page_not_found)
                ).show()
            }
        }
    }

    private fun initialiseCardAdapter() {
        comicStripAdapter.comicActionListener = object : ComicStripAdapter.ComicActionListener {
            override fun onImageClicked(uiComicStrip: UiComicStrip) {
                onItemClicked(uiComicStrip)
            }

            override fun onItemFavouriteChanged(id: Int, isFavourite: Boolean) {
                viewModel.toggleFavourite(id, isFavourite)
            }
        }
        card_stack_view.apply {
            adapter = comicStripAdapter
            layoutManager = comicStripLayoutManager
        }
    }

    private fun onItemClicked(item: UiComicStrip) {
        ImageViewer.Builder<String>(requireActivity(), listOf(item.imageLink))
            .hideStatusBar(false)
            .setImageMargin(activity, R.dimen.margin_16dp)
            .show()
    }

    private fun onError(uiError: UiError?) {
        if (uiError != null) {
            view_flipper.displayedChild = INDEX_CONTENT
            Toasty.error(requireActivity(), getString(uiError.messageResId)).show()
            viewModel.clearError()
        }
    }
}
