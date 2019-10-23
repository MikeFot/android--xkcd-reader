/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2019.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.comics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.dialog.DialogFactory
import com.michaelfotiads.xkcdreader.ui.fragment.comics.binder.ViewActionCallbacks
import com.michaelfotiads.xkcdreader.ui.fragment.comics.binder.ViewBinder
import com.michaelfotiads.xkcdreader.ui.fragment.comics.model.ComicAction
import com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel.ComicsViewModel
import com.michaelfotiads.xkcdreader.ui.fragment.comics.viewmodel.ComicsViewModelFactory
import com.michaelfotiads.xkcdreader.ui.image.ImageLoader
import com.michaelfotiads.xkcdreader.ui.intent.IntentDispatcher
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.view.base.extensionTintMenuItems
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
    lateinit var imageHelper: ImageLoader
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
        inflater.inflate(R.menu.menu_comics, menu)
        menu.extensionTintMenuItems(requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        binder.callbacks = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder = ViewBinder(view, imageHelper)
        binder.callbacks = object : ViewActionCallbacks {
            override fun onItemAppeared(uiComicStrip: UiComicStrip) {
                viewModel.setCurrentItem(uiComicStrip)
            }

            override fun onErrorShown() {
                viewModel.clearError()
            }

            override fun toggleFavourite(comicStripId: Int, isFavourite: Boolean) {
                viewModel.toggleFavourite(comicStripId, isFavourite)
            }
        }
        binder.initialiseCardAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.run {
            pagedItems.observe(viewLifecycleOwner, Observer(binder::setAdapterContent))
            actionLiveData.observe(viewLifecycleOwner, Observer(this@ComicsFragment::processAction))
            lastLoadedIndex.observe(viewLifecycleOwner, Observer(viewModel::loadComic))
        }
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
                requireActivity(),
                appDialog.maxStripIndex,
                viewModel::setSearchParameter
            )
            is AppDialog.About -> dialogFactory.showAboutDialog(requireActivity())
        }
    }

    private fun resetAndLoadData() {
        binder.showGettingLatest()
        viewModel.refresh()
    }
}
