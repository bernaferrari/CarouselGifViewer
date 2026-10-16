package com.bernaferrari.carouselgifviewer.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

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
import androidx.compose.material3.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.muted
import platform.AVFoundation.rate
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.QuartzCore.CALayer
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.UIKit.UIView
import platform.QuartzCore.CATransaction
import platform.CoreGraphics.CGRectZero
import kotlinx.cinterop.readValue
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier,
    playbackSpeed: Float,
    posterUrl: String?,
    playbackEnabled: Boolean,
    preferences: PlaybackPreferences,
) {
    var isBuffering by remember(videoUrl) { mutableStateOf(true) }
    var playbackFailed by remember(videoUrl) { mutableStateOf(false) }
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val canPlay = playbackEnabled && preferences.isPlayingRequested && lifecycleState == Lifecycle.State.RESUMED
    val currentSpeed by rememberUpdatedState(playbackSpeed)
    val currentPlaybackEnabled by rememberUpdatedState(canPlay)
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
    
    var firstFrameRendered by remember(videoUrl) { mutableStateOf(false) }
    val player = remember(videoUrl) {
        val url = NSURL.URLWithString(videoUrl)!!
        val playerItem = AVPlayerItem(url)
        AVPlayer(playerItem).apply {
            muted = preferences.isMuted
            seekToTime(CMTimeMakeWithSeconds(preferences.positionMillis / 1000.0, 600))
        }
    }
    
    val videoLayer = remember(player) { AVPlayerLayer.playerLayerWithPlayer(player) }

    DisposableEffect(player) {
        val center = NSNotificationCenter.defaultCenter
        
        // Loop playback
        val loopObserver = center.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            player.seekToTime(CMTimeMakeWithSeconds(0.0, 1000000000))
            if (currentPlaybackEnabled) {
                player.play()
                player.rate = currentSpeed
            }
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
            if (currentPlaybackEnabled) {
                player.play()
                player.rate = currentSpeed
            }
        }
        
        if (currentPlaybackEnabled) {
            player.play()
            player.rate = currentSpeed
        }

        onDispose {
            val seconds = CMTimeGetSeconds(player.currentTime())
            if (seconds.isFinite()) preferences.positionMillis = (seconds * 1000).toLong().coerceAtLeast(0L)
            center.removeObserver(loopObserver)
            center.removeObserver(willResign)
            center.removeObserver(didBecome)
            player.pause()
        }
    }
    
    LaunchedEffect(player, playbackSpeed, canPlay) {
        if (canPlay) {
            player.play()
            player.rate = playbackSpeed
        } else {
            player.pause()
        }
    }

    LaunchedEffect(player) {
        while (isActive) {
            firstFrameRendered = videoLayer.readyForDisplay
            playbackFailed = player.currentItem?.status == AVPlayerItemStatusFailed
            isBuffering = !playbackFailed &&
                (!firstFrameRendered || player.timeControlStatus == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate)
            delay(200)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        @Suppress("DEPRECATION")
        UIKitView(
            factory = {
                VideoContainer(videoLayer)
            },
            update = { view ->
                // Update layer frame when view bounds change
                videoLayer.frame = view.bounds
            },
            modifier = Modifier.fillMaxSize()
        )
        
        if (!firstFrameRendered && posterUrl != null) {
            KmpAsyncImage(posterUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
        }

        if (playbackFailed) {
            Column(
                modifier = Modifier.align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Video unavailable", color = MaterialTheme.colorScheme.onSurface)
                FilledTonalButton(onClick = {
                    firstFrameRendered = false
                    playbackFailed = false
                    player.replaceCurrentItemWithPlayerItem(AVPlayerItem(NSURL.URLWithString(videoUrl)!!))
                    player.play()
                    player.rate = currentSpeed
                }) { Text("Retry") }
            }
        }

        // Loading indicator overlay (only shows after 250ms delay)
        AnimatedVisibility(
            visible = playbackEnabled && showLoading && isBuffering,
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


/** UIKit lays out interop views after Compose's update callback has run. */
@OptIn(ExperimentalForeignApi::class)
private class VideoContainer(private val videoLayer: AVPlayerLayer) : UIView(frame = CGRectZero.readValue()) {
    init {
        videoLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        layer.addSublayer(videoLayer)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        videoLayer.frame = bounds
        CATransaction.commit()
    }
}
