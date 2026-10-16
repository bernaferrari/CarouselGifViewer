package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** User intent outlives a native player instance, a short scrub, and activity recreation. */
@Stable
internal class PlaybackPreferences {
    var isPlayingRequested by mutableStateOf(true)
    var isMuted by mutableStateOf(true)
    var positionMillis: Long = 0L
}
