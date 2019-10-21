package com.michaelfotiads.xkcdreader.ui.dialog

import android.app.Activity
import android.text.InputType
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.michaelfotiads.xkcdreader.R
import com.yarolegovich.lovelydialog.LovelyInfoDialog
import com.yarolegovich.lovelydialog.LovelyTextInputDialog
import javax.inject.Inject

internal class DialogFactory @Inject constructor() {

    fun showSearch(activity: Activity, maxStripIndex: Int, callback: (String, Int) -> Unit) {
        LovelyTextInputDialog(activity)
            .setTopColorRes(R.color.primary_dark)
            .setIcon(R.drawable.ic_search_black_24dp)
            .setIconTintColor(ContextCompat.getColor(activity, R.color.white))
            .setMessage(activity.getString(R.string.dialog_search_title))
            .setHint(activity.getString(R.string.dialog_search_hint, maxStripIndex.toString()))
            .setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
            .setConfirmButtonColor(
                ContextCompat.getColor(
                    activity,
                    R.color.secondary_text
                )
            )
            .setConfirmButton(android.R.string.ok) { query -> callback.invoke(query, maxStripIndex) }
            .show()
    }

    fun showAboutDialog(activity: Activity) {
        LovelyInfoDialog(activity)
            .setTopColorRes(R.color.primary_dark)
            .setIcon(R.drawable.ic_info_outline_black_24dp)
            .setIconTintColor(ContextCompat.getColor(activity, R.color.white))
            .setMessageGravity(Gravity.CENTER_HORIZONTAL)
            .setTitle(R.string.info_title)
            .setMessage(R.string.info_message)
            .show()
    }
}
