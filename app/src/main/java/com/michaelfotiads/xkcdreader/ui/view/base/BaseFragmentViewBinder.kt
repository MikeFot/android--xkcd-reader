package com.michaelfotiads.xkcdreader.ui.view.base

import android.content.Context
import android.view.View

internal abstract class BaseFragmentViewBinder<V : BaseFragmentViewHolder>(view: View) {

    val context: Context = view.context
    abstract val viewHolder: V
}
