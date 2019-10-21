/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2019.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.fragment.favourites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.binder.FavouritesViewBinder
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.interactor.LoadFavouritePagesInteractor
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FavouritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavouritesFragment()
    }

    private lateinit var pagedList: LiveData<PagedList<UiComicStrip>>
    @Inject
    lateinit var interactor: LoadFavouritePagesInteractor

    private lateinit var binder: FavouritesViewBinder

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
        binder = FavouritesViewBinder(view)
        binder.initialiseAdapter()

        pagedList = LivePagedListBuilder(interactor.getPages(), 10)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<UiComicStrip>() {
            })
            .build()

        pagedList.observe(viewLifecycleOwner, Observer(binder::setItems))
    }
}
