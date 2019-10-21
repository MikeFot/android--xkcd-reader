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
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.dialog.DialogFactory
import com.michaelfotiads.xkcdreader.ui.fragment.comics.binder.ViewBinder
import com.michaelfotiads.xkcdreader.ui.fragment.comics.model.ComicAction
import com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel.ComicsViewModel
import com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel.ComicsViewModelFactory
import com.michaelfotiads.xkcdreader.ui.intent.IntentDispatcher
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

internal class ComicsFragment : Fragment() {

    companion object {
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

    private lateinit var binder: ViewBinder

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
        binder = ViewBinder(view)
        binder.callbacks = object : ViewBinder.ViewActionCallbacks {
            override fun onItemAppeared(uiComicStrip: UiComicStrip) {
                viewModel.setCurrentItem(uiComicStrip)
            }

            override fun onErrorShown() {
                viewModel.clearError()
            }

            override fun toggleFavourite(id: Int, favourite: Boolean) {
                viewModel.toggleFavourite(id, favourite)
            }

        }
        binder.initialiseCardAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.pagedItems.observe(viewLifecycleOwner, Observer(binder::setAdapterContent))
        viewModel.actionLiveData.observe(viewLifecycleOwner, Observer(this::processAction))
        viewModel.lastLoadedIndex.observe(viewLifecycleOwner, Observer(viewModel::loadComic))
    }

    private fun processAction(action: ComicAction) {
        when (action) {
            is ComicAction.ShowContent -> binder.showContent()
            is ComicAction.ShowError -> binder.onError(action.uiError)
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
                requireActivity(), appDialog.maxStripIndex, this::processSearchQuery)
            is AppDialog.About -> dialogFactory.showAboutDialog(requireActivity())
        }
    }

    private fun resetAndLoadData() {
        binder.showGettingLatest()
        viewModel.refresh()
    }

    private fun processSearchQuery(query: String?, maxStripIndex: Int) {
        if (!query.isNullOrBlank()) {
            val stripNumber = query.toInt()
            if (stripNumber in 1..maxStripIndex) {
                viewModel.setSearchParameter(stripNumber)
            } else {
                binder.showPageNotFound()
            }
        }
    }
}
