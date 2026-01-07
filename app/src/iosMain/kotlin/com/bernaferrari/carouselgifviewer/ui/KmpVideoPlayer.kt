package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.ui.ExperimentalComposeUiApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerViewController
import platform.AVFoundation.play
import platform.Foundation.NSURL

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier
) {
    val player = remember {
        val url = NSURL.URLWithString(videoUrl)!!
        val playerItem = AVPlayerItem(url)
        AVPlayer(playerItem)
    }

    DisposableEffect(Unit) {
        player.play()
        onDispose {
            player.pause()
        }
    }

    UIKitView(
        factory = { ctx ->
            AVPlayerViewController().apply {
                this.player = player
                showsPlaybackControls = false
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
