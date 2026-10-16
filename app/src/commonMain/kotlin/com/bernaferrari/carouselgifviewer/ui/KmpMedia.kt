package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktor.http.Url
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath

@Composable
expect fun KmpAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
)

@Composable
expect fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    playbackSpeed: Float = 1f
)

// Giphy provides the first frame separately; the strip never needs the full animation.
internal fun String.toStillThumbnailUrl(): String {
    if (!startsWith("https://", ignoreCase = true)) return this
    val url = runCatching { Url(this) }.getOrNull() ?: return this
    if (!Regex("media[0-9]*\\.giphy\\.com").matches(url.host.lowercase()) ||
        !url.encodedPath.endsWith("/giphy.gif")
    ) return this
    return URLBuilder(url).apply {
        encodedPath = url.encodedPath.removeSuffix("/giphy.gif") + "/giphy_s.gif"
    }.buildString()
}
