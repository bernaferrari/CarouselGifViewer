package com.bernaferrari.carouselgifviewer.handler

import android.content.Context
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.widget.Toast
import androidx.core.net.toUri
import com.bernaferrari.carouselgifviewer.R

internal class OpenBrowserItemHandler(
    private val context: Context,
    private val url: String
) : ItemHandler {
    override fun invoke() {
        if (url.isNotBlank()) {
            CustomTabsIntent.Builder()
                .setToolbarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_blue_A200
                    )
                )
                .addDefaultShareMenuItem()
                .build()
                .launchUrl(context, url.toUri())
        } else {
            Toast.makeText(context, "Error...", Toast.LENGTH_SHORT).show()
        }
    }
}