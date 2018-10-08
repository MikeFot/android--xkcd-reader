/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.yarolegovich.lovelydialog.LovelyTextInputDialog
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

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var viewModelFactory: MainViewModelFactory

    private val uniqueId = UUID.randomUUID().toString()

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: StripAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(uniqueId, MainViewModel::class.java)
        setContentView(R.layout.activity_main)

        initialiseCardAdapter()

        viewModel.resultsData.observe(this, Observer { onResult(it) })

        viewModel.errorData.observe(this, Observer { onError(it) })

        viewModel.searchData.observe(this, Observer { showSearch(it!!) })

        viewModel.resetAdapterData.observe(this, Observer { onResetAdapter(it!!) })

        viewModel.loadInitialData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val tintColor = ContextCompat.getColor(this, R.color.white)

        for (i in 0 until menu.size()) {
            menu.getItem(i).icon?.run {
                mutate()
                setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_refresh -> {
                resetAndLoadData()
                true
            }
            R.id.menu_item_search  -> {
                viewModel.showSearch()
                true
            }
            else                   -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSearch(maxStripIndex: Int) {
        val icon: Drawable = ContextCompat.getDrawable(this, R.drawable.ic_search_black_24dp)!!
        icon.mutate()
        icon.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white),
                            PorterDuff.Mode.SRC_ATOP)

        LovelyTextInputDialog(this)
                .setTopColorRes(R.color.primary_dark)
                .setIcon(icon)
                .setMessage(getString(R.string.dialog_search_title))
                .setHint(getString(R.string.dialog_search_hint, maxStripIndex.toString()))
                .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .setConfirmButton(android.R.string.ok) {
                    it.isNotBlank().run {
                        val stripNumber = it.toInt()
                        if (stripNumber in 1..maxStripIndex) {
                            viewModel.setSearchParameter(stripNumber)
                        } else {
                            Toasty.error(
                                    this@MainActivity,
                                    getString(R.string.message_page_not_found)).show()
                        }
                    }
                }
                .show()
    }

    private fun resetAndLoadData() {
        Toasty.info(this, getString(R.string.message_reset_data)).show()
        viewModel.resetData()
        viewModel.loadInitialData()
    }

    private fun initialiseCardAdapter() {
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
                onItemSwiped(direction)
            }
        })
    }

    private fun onItemSwiped(direction: SwipeDirection?) {
        Timber.d("On Card Swiped $direction and we are now at top index ${card_stack_view.topIndex}")
        // remove the item so it won't refresh with the adapter
        card_stack_view.post { adapter.remove(adapter.getItem(card_stack_view.topIndex - 1)) }
        if (adapter.count == 0) {
            Toasty.success(this@MainActivity, getString(R.string.message_all_caught_up)).show()
        } else {
            // remove the item so it won't refresh with the adapter
            viewModel.decrementCurrentStrip()
            viewModel.loadAdditionalData(adapter.count)
        }
    }

    private fun onItemClicked(item: UiComicStrip) {
        // use Fresco!
        ImageViewer.Builder<String>(this, listOf(item.imageLink))
                .setImageMargin(this, R.dimen.margin_16dp)
                .show()
    }

    private fun onResetAdapter(shouldReset: Boolean) {
        if (shouldReset) {
            Timber.d("Resetting adapter data")
            card_stack_view.post {
                adapter.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun onResult(comicStrip: UiComicStrip) {
        Timber.d("Loaded item with ID ${comicStrip.number}")
        card_stack_view.post {
            adapter.add(comicStrip)
            Timber.d("Adapter has ${adapter.count} items")
            viewModel.loadAdditionalData(adapter.count)
        }
    }

    private fun onError(uiError: UiError?) {
        uiError?.let {
            Toasty.error(this, getString(uiError.messageResId)).show()
            viewModel.clearError()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }
}
