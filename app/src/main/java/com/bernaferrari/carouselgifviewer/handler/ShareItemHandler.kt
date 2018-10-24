package com.bernaferrari.carouselgifviewer.handler

import android.app.Activity
import androidx.core.app.ShareCompat.IntentBuilder
import com.bernaferrari.carouselgifviewer.R

internal class ShareItemHandler(
    private val activity: Activity,
    private val text: String
) : ItemHandler {
    override fun invoke() {
        IntentBuilder.from(activity)
            .setType("text/plain")
            .setChooserTitle(activity.getString(R.string.share))
            .setText(text)
            .startChooser()
    }
}
