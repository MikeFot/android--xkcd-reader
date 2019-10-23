/*
 * Developed by Michail Fotiadis.
 * Copyright (c) 2018.
 * All rights reserved.
 */

package com.michaelfotiads.xkcdreader.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.michaelfotiads.xkcdreader.R

class FavouriteImageView : ImageView {

    constructor(context: Context) : super(context) {
        setup(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup(context, attrs, defStyleAttr, 0)
    }

    @Suppress("unused")
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setup(context, attrs, defStyleAttr, defStyleRes)
    }

    private var iconOn: Drawable? = null
    private var iconOff: Drawable? = null

    private fun setup(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FavouriteImageView,
                defStyleAttr,
                defStyleRes
            )

            val tintColor = typedArray.getColor(R.styleable.FavouriteImageView_iconTint, 0)
            typedArray.getDrawable(R.styleable.FavouriteImageView_iconOn)?.let { drawable ->
                tintAndSetIconOn(tintColor, drawable)
            }
            typedArray.getDrawable(R.styleable.FavouriteImageView_iconOff)?.let { drawable ->
                tintAndSetIconOff(tintColor, drawable)
            }
            typedArray.recycle()
        }

        if (isInEditMode) {
            setFavourite(false)
        }
    }

    private fun tintAndSetIconOn(tintColor: Int, drawable: Drawable) {
        iconOn = drawable.apply {
            if (tintColor != 0) {
                setTint(tintColor)
            }
        }
    }

    private fun tintAndSetIconOff(tintColor: Int, drawable: Drawable) {
        iconOff = drawable.apply {
            if (tintColor != 0) {
                setTint(tintColor)
            }
        }
    }

    fun setFavourite(isFavourite: Boolean) {
        if (isFavourite) {
            iconOn?.let(this::setImageDrawable)
        } else {
            iconOff?.let(this::setImageDrawable)
        }
    }
}
