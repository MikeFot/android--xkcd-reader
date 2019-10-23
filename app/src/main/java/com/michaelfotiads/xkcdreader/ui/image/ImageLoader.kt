package com.michaelfotiads.xkcdreader.ui.image

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.michaelfotiads.xkcdreader.di.GlideApp
import com.michaelfotiads.xkcdreader.di.GlideRequest
import com.michaelfotiads.xkcdreader.di.GlideRequests
import com.stfalcon.frescoimageviewer.ImageViewer

class ImageLoader(context: Context) {

    private val glideAppRequests = GlideApp.with(context.applicationContext)

    private val fullRequest = glideAppRequests
        .asDrawable()
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .placeholder(ColorDrawable(Color.GRAY))

    fun preLoadGlideImage(link: String): GlideRequest<Drawable> {
        return fullRequest.load(link)
    }

    fun loadGlideIntoImageView(link: String, view: ImageView) {
        GlideApp.with(view)
            .asDrawable()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .load(link)
            .into(view)
    }

    fun showFrescoImage(
        view: View,
        link: String,
        marginDimension: Int
    ) {
        ImageViewer.Builder<String>(view.context, listOf(link))
            .hideStatusBar(false)
            .setImageMargin(view.context, marginDimension)
            .show()
    }

    fun <T> generateRecyclerViewPreLoader(
        view: View,
        listPreLoader: ListPreloader.PreloadModelProvider<T>,
        numberOfItemsToPreLoad: Int
    ): RecyclerViewPreloader<T> {
        val preloadSizeProvider = ViewPreloadSizeProvider<T>()
        return RecyclerViewPreloader<T>(
            getGlideViewRequest(view),
            listPreLoader,
            preloadSizeProvider,
            numberOfItemsToPreLoad)
    }

    private fun getGlideViewRequest(view: View): GlideRequests {
        return GlideApp.with(view)
    }
}
