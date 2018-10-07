/*
 * Developed by Michail Fotiadis on 07/10/18 18:03.
 * Last modified 07/10/18 18:03.
 * Copyright (c) 2018. All rights reserved.
 *
 *
 */

package com.michaelfotiads.xkcdreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.adapter.StripAdapter
import com.michaelfotiads.xkcdreader.ui.error.UiError
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModel
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModelFactory
import com.stfalcon.frescoimageviewer.ImageViewer
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.card_stack_view
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

private const val MIN_IMAGES = 5

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private val uniqueId = UUID.randomUUID().toString()

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: StripAdapter
    private var lastItemLoadedIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(uniqueId, MainViewModel::class.java)
        setContentView(R.layout.activity_main)

        adapter = StripAdapter(this)
        card_stack_view.setAdapter(adapter)
        card_stack_view.setCardEventListener(object : CardStackView.CardEventListener {
            override fun onCardDragging(percentX: Float, percentY: Float) {
                // nothing
            }

            override fun onCardReversed() {
                Timber.d("On Card Reversed")
            }

            override fun onCardMovedToOrigin() {
                Timber.d("On Card Moved to Origin")
            }

            override fun onCardClicked(index: Int) {
                Timber.d("On Card Clicked Index $index")
                onItemClicked(adapter.getItem(index)!!)
            }

            override fun onCardSwiped(direction: SwipeDirection?) {
                Timber.d("On Card Swiped $direction and we are now at top index ${card_stack_view.topIndex}")
                // remove the item so it won't refresh with the adapter
                adapter.remove(adapter.getItem(card_stack_view.topIndex - 1))
                loadNext()
            }
        })
        viewModel.resultsData.observe(this, Observer { onResult(it) })

        viewModel.errorData.observe(this, Observer { onError(it) })

        viewModel.loadInitialData()
    }

    private fun onItemClicked(item: UiComicStrip) {
        // use Fresco!
        ImageViewer.Builder<String>(this, listOf(item.imageLink))
                .setImageMargin(this, R.dimen.margin_16dp)
                .show()
    }

    private fun loadNext() {
        if (lastItemLoadedIndex > 1 && adapter.count < MIN_IMAGES) {
            Timber.d("Loading Next")
            viewModel.loadAdditionalData(lastItemLoadedIndex - 1)
        } else {
            Timber.d("No more to load")
        }
    }

    private fun onResult(comicStrip: UiComicStrip) {
        Timber.d("Loaded item with ID ${comicStrip.number}")
        lastItemLoadedIndex = comicStrip.number
        adapter.add(comicStrip)
        Timber.d("Adapter has ${adapter.count} items")
        loadNext()
    }

    private fun onError(uiError: UiError) {
        Toasty.error(this, getString(uiError.messageResId)).show()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }
}
