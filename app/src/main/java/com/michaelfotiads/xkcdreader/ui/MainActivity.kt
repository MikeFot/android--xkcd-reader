/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.fragment.comics.ComicsFragment
import com.michaelfotiads.xkcdreader.ui.fragment.favourites.FavouritesFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var comicsFragment: ComicsFragment
    private lateinit var favouritesFragment: FavouritesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        comicsFragment = ComicsFragment.newInstance()
        favouritesFragment = FavouritesFragment.newInstance()

        val pagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }
}
