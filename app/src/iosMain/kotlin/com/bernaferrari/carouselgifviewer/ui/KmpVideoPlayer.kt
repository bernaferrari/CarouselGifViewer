package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.ui.ExperimentalComposeUiApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.kCMTimeZero
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.QuartzCore.CALayer
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.addObserver
import platform.Foundation.removeObserver

@OptIn(ExperimentalComposeUiApi::class, ExperimentalForeignApi::class)
@Composable
actual fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier
) {
    var isBuffering by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    
    // Only show loading after 250ms delay (skip for cached videos)
    LaunchedEffect(isBuffering) {
        if (isBuffering) {
            delay(250)
            showLoading = true
        } else {
            showLoading = false
        }
    }
    
    val player = remember(videoUrl) {
        val url = NSURL.URLWithString(videoUrl)!!
        val playerItem = AVPlayerItem(url)
        AVPlayer(playerItem).apply {
            // Configure forward buffer for caching (approximately 100MB worth)
            // AVPlayer uses preferredForwardBufferDuration in seconds
            // Assuming ~2MB/s video, 100MB = ~50 seconds of buffer
            currentItem?.preferredForwardBufferDuration = 50.0
        }
    }
    
    DisposableEffect(player) {
        val center = NSNotificationCenter.defaultCenter
        
        // Observe player time control status for loading state
        val timeObserver = player.addPeriodicTimeObserverForInterval(
            interval = CMTimeMake(1, 10),
            queue = null
        ) { _ ->
            val status = player.timeControlStatus
            isBuffering = status == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
        }
        
        // Loop playback
        val loopObserver = center.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            player.seekToTime(kCMTimeZero)
            player.play()
        }
        
        // App lifecycle handling
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
            player.removeTimeObserver(timeObserver)
            center.removeObserver(loopObserver)
            center.removeObserver(willResign)
            center.removeObserver(didBecome)
            player.pause()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        UIKitView(
            factory = {
                val container = UIView()
                val playerLayer = AVPlayerLayer.playerLayerWithPlayer(player)
                playerLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
                container.layer.addSublayer(playerLayer)
                container
            },
            update = { view ->
                // Update layer frame when view bounds change
                val playerLayer = view.layer.sublayers?.firstOrNull() as? AVPlayerLayer
                playerLayer?.frame = view.bounds
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Loading indicator overlay (only shows after 250ms delay)
        AnimatedVisibility(
            visible = showLoading && isBuffering,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
