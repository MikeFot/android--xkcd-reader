/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.fragment.comics.ComicsFragment
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.FavouritesFragment
import com.michaelfotiads.xkcdreader.ui.intent.IntentDispatcher
import com.michaelfotiads.xkcdreader.ui.model.AppDialog
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModel
import com.michaelfotiads.xkcdreader.ui.viewmodel.MainViewModelFactory
import com.yarolegovich.lovelydialog.LovelyInfoDialog
import com.yarolegovich.lovelydialog.LovelyTextInputDialog
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import es.dmoral.toasty.Toasty
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    @Inject
    lateinit var intentDispatcher: IntentDispatcher

    private lateinit var viewModel: MainViewModel

    private lateinit var dialogFactory: DialogFactory
    private lateinit var comicsFragment: ComicsFragment
    private lateinit var favouritesFragment: FavouritesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)

        comicsFragment = ComicsFragment.newInstance()
        favouritesFragment = FavouritesFragment.newInstance()

        val pagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {

            val items = listOf(comicsFragment, favouritesFragment)

            override fun getItem(position: Int): Fragment = items[position]

            override fun getCount(): Int = items.size
        }

        view_pager.adapter = pagerAdapter

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_comics -> {
                    view_pager.currentItem = 0
                    true
                }
                R.id.action_favourites -> {
                    view_pager.currentItem = 1
                    true
                }
                else -> false
            }
        }
        bottom_navigation_view.selectedItemId = R.id.action_comics

        dialogFactory = DialogFactory()

        viewModel.dialogData.observe(this, Observer {
            when (it) {
                is AppDialog.Search -> {
                    dialogFactory.showSearch(it.id)
                }
                is AppDialog.About -> {
                    dialogFactory.showAboutDialog()
                }
            }
        })
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
            R.id.menu_item_search -> {
                viewModel.showSearch()
                true
            }
            R.id.menu_item_about -> {
                viewModel.showAboutDialog()
                true
            }
            R.id.menu_item_share -> {
                shareItem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareItem() {
        if (view_pager.currentItem == 0) {
            comicsFragment.getTopItem()?.let(intentDispatcher::share)
        }
    }

    private fun processSearchQuery(it: String?, maxStripIndex: Int) {
        if (!it.isNullOrBlank()) {
            val stripNumber = it.toInt()
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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }

    inner class DialogFactory {

        internal fun showSearch(maxStripIndex: Int) {
            LovelyTextInputDialog(this@MainActivity)
                .setTopColorRes(R.color.primary_dark)
                .setIcon(R.drawable.ic_search_black_24dp)
                .setIconTintColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                .setMessage(getString(R.string.dialog_search_title))
                .setHint(getString(R.string.dialog_search_hint, maxStripIndex.toString()))
                .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
                .setConfirmButtonColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.secondary_text
                    )
                )
                .setConfirmButton(android.R.string.ok) { processSearchQuery(it, maxStripIndex) }
                .show()
        }

        internal fun showAboutDialog() {
            LovelyInfoDialog(this@MainActivity)
                .setTopColorRes(R.color.primary_dark)
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setIconTintColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                .setMessageGravity(Gravity.CENTER_HORIZONTAL)
                .setTitle(R.string.info_title)
                .setMessage(R.string.info_message)
                .show()
        }
    }
}
