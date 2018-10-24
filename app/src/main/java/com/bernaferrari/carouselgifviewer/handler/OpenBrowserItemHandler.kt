package com.bernaferrari.carouselgifviewer.handler

//import androidx.browser.customtabs.CustomTabsIntent
import android.content.Context
import android.widget.Toast

internal class OpenBrowserItemHandler(
    private val context: Context,
    private val url: String
) : ItemHandler {
    override fun invoke() {
        if (url.isNotBlank()) {
//            CustomTabsIntent.Builder()
//                .setToolbarColor(ContextCompat.getColor(context, R.color.md_blue_A200))
//                .addDefaultShareMenuItem()
//                .build()
//                .launchUrl(context, url.toUri())
        } else {
            Toast.makeText(context, "Error...", Toast.LENGTH_SHORT).show()
        }
    }
}