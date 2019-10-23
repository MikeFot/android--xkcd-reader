/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2019.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.favourites

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
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.binder.FavouritesViewBinder
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.binder.ViewActionCallbacks
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.model.FavouritesAction
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.viewmodel.FavouritesViewModel
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.viewmodel.FavouritesViewModelFactory
import com.michaelfotiads.xkcdreader.ui.image.ImageLoader
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.view.base.extensionTintMenuItems
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

internal class FavouritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavouritesFragment()
    }

    @Inject
    lateinit var factory: FavouritesViewModelFactory
    @Inject
    lateinit var dialogFactory: DialogFactory
    @Inject
    lateinit var imageLoader: ImageLoader

    private val viewModel: FavouritesViewModel by lazy {
        ViewModelProviders.of(this, factory).get(FavouritesViewModel::class.java)
    }

    private lateinit var binder: FavouritesViewBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_favourites, menu)
        menu.extensionTintMenuItems(requireContext())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_about -> {
                viewModel.showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder = FavouritesViewBinder(view, imageLoader)
        binder.callbacks = object : ViewActionCallbacks {
            override fun toggleFavourite(comicStripId: Int, isFavourite: Boolean) {
                viewModel.toggleFavourite(comicStripId, isFavourite)
            }
        }
        binder.initialiseAdapter()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binder.callbacks = null
    }

    private fun setupObservers() {
        viewModel.run {
            pagedList.observe(viewLifecycleOwner, Observer(binder::setItems))
            actionLiveData.observe(viewLifecycleOwner, Observer(this@FavouritesFragment::processAction))
        }
    }

    private fun processAction(action: FavouritesAction) {
        when (action) {
            is FavouritesAction.ShowDialog -> showDialog(action.appDialog)
            is FavouritesAction.Idle -> {
                // NOOP
            }
        }
    }

    private fun showDialog(appDialog: AppDialog?) {
        when (appDialog) {
            is AppDialog.About -> dialogFactory.showAboutDialog(requireActivity())
            else -> {
                // NOOP
            }
        }
    }
}
