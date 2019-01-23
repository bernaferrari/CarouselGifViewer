package com.bernaferrari.carouselgifviewer.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bernaferrari.carouselgifviewer.R

internal inline fun View.onKey(crossinline body: (KeyEvent) -> Boolean) {
    setOnKeyListener { _, _, event -> body(event) }
}

internal fun Activity.getScreenPercentSize(percent: Float = 0.7f): Int {
    val windowDimensions = Point()
    this.windowManager.defaultDisplay.getSize(windowDimensions)
    return Math.round(Math.min(windowDimensions.y, windowDimensions.x) * percent)
}

fun Activity.shareItemHandler(text: String, content: String) {
    ShareCompat.IntentBuilder.from(this)
        .setType("text/plain")
        .setChooserTitle(this.getString(R.string.share, text))
        .setText(content)
        .startChooser()
}

fun Context.openBrowserItemHandler(url: String) {
    if (url.isNotBlank()) {
        CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.md_blue_A200))
            .addDefaultShareMenuItem()
            .build()
            .launchUrl(this, url.toUri())
    } else {
        Toast.makeText(this, "Error...", Toast.LENGTH_SHORT).show()
    }
}
