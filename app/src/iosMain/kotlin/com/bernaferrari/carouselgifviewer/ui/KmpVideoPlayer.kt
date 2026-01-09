package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import org.jetbrains.compose.ui.ExperimentalComposeUiApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerViewController
import platform.Foundation.NSURL
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification

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
        val center = NSNotificationCenter.defaultCenter
        val willResign = center.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            player.pause()
        }
        val didBecome = center.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            player.play()
        }
        player.play()
        onDispose {
            center.removeObserver(willResign)
            center.removeObserver(didBecome)
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
