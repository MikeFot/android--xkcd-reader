package com.michaelfotiads.xkcdreader.ui.intent

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.core.app.ShareCompat
import com.michaelfotiads.xkcdreader.R
import com.michaelfotiads.xkcdreader.ui.model.UiComicStrip
import javax.inject.Inject

private const val PATH = ".ui.MainActivity"
private const val TYPE = "text/plain"

internal class IntentDispatcher @Inject constructor() {

    fun share(activity: Activity, uiComicStrip: UiComicStrip) {
        val packageManager = activity.applicationContext.packageManager
        val componentName = ComponentName(activity.packageName, "${activity.packageName}$PATH")
        packageManager.setComponentEnabledSetting(componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP)

        val shareText = activity.getString(R.string.share_info, uiComicStrip.title, uiComicStrip.shareLink)
        ShareCompat.IntentBuilder.from(activity)
            .setType(TYPE)
            .setChooserTitle(activity.getString(R.string.share_title))
            .setText(shareText)
            .startChooser()
        packageManager.setComponentEnabledSetting(componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP)
    }
}
