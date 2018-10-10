/*
 * Developed by Michail Fotiadis on 08/10/18 14:35.
 * Last modified 08/10/18 14:34.
 * Copyright (c) 2018. All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
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
import com.yarolegovich.lovelydialog.LovelyInfoDialog
import com.yarolegovich.lovelydialog.LovelyTextInputDialog
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.card_stack_view
import kotlinx.android.synthetic.main.activity_main.view_flipper
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

    companion object {
        private const val INDEX_PROGRESS = 0
        private const val INDEX_CONTENT = 1
    }

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
            R.id.menu_item_about   -> {
                showAboutDialog()
                true
            }
            R.id.menu_item_share   -> {
                shareItem()
                true
            }
            else                   -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        LovelyInfoDialog(this)
            .setTopColorRes(R.color.primary_dark)
            .setIcon(R.drawable.ic_info_outline_black_24dp)
            .setIconTintColor(ContextCompat.getColor(this, R.color.white))
            .setMessageGravity(Gravity.CENTER_HORIZONTAL)
            .setTitle(R.string.info_title)
            .setMessage(R.string.info_message)
            .show()
    }

    private fun showSearch(maxStripIndex: Int) {
        LovelyTextInputDialog(this)
            .setTopColorRes(R.color.primary_dark)
            .setIcon(R.drawable.ic_search_black_24dp)
            .setIconTintColor(ContextCompat.getColor(this, R.color.white))
            .setMessage(getString(R.string.dialog_search_title))
            .setHint(getString(R.string.dialog_search_hint, maxStripIndex.toString()))
            .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
            .setConfirmButtonColor(ContextCompat.getColor(this, R.color.secondary_text))
            .setConfirmButton(android.R.string.ok) { processSearchQuery(it, maxStripIndex) }
            .show()
    }

    private fun shareItem() {
        if (adapter.count > 0) {
            adapter.getItem(0)?.let {

                val shareText = getString(R.string.share_info, it.title, it.imageLink)

                ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle(getString(R.string.share_title))
                    .setText(shareText)
                    .startChooser()
            }
        }
    }

    private fun processSearchQuery(it: String?, maxStripIndex: Int) {
        if (!it.isNullOrBlank()) {
            val stripNumber = it!!.toInt()
            if (stripNumber in 1..maxStripIndex) {
                viewModel.setSearchParameter(stripNumber)
            } else {
                Toasty.error(
                    this@MainActivity,
                    getString(R.string.message_page_not_found)
                ).show()
            }
        }
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
        adapter.remove(adapter.getItem(card_stack_view.topIndex - 1))
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
            .hideStatusBar(false)
            .setImageMargin(this, R.dimen.margin_16dp)
            .show()
    }

    private fun onResetAdapter(shouldReset: Boolean) {
        if (shouldReset) {
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
            Toasty.error(this, getString(uiError.messageResId)).show()
            viewModel.clearError()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }
}
