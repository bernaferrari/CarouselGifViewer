package com.bernaferrari.carouselgifviewer.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.bernaferrari.carouselgifviewer.ui.icons.AppIcons
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.io.File

/**
 * Singleton cache manager for video caching
 */
@OptIn(UnstableApi::class)
object VideoCacheManager {
    private var cache: SimpleCache? = null
    private const val CACHE_SIZE_BYTES = 100L * 1024L * 1024L // 100MB
    
    @Synchronized
    fun getCache(context: Context): SimpleCache {
        if (cache == null) {
            val cacheDir = File(context.cacheDir, "video_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE_BYTES)
            val databaseProvider = StandaloneDatabaseProvider(context)
            cache = SimpleCache(cacheDir, evictor, databaseProvider)
        }
        return cache!!
    }
    
    @Synchronized
    fun release() {
        cache?.release()
        cache = null
    }
}

@OptIn(UnstableApi::class)
@Composable
actual fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier,
    playbackSpeed: Float
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isBuffering by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(true) } // Start muted by default
    var isPlaying by remember { mutableStateOf(false) }
    
    // Only show loading after 250ms delay (skip for cached videos)
    LaunchedEffect(isBuffering) {
        if (isBuffering) {
            delay(250)
            showLoading = true
        } else {
            showLoading = false
        }
    }
    
    // Auto-hide controls after 3 seconds
    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(3000)
            controlsVisible = false
        }
    }

    val exoPlayer = remember(videoUrl) {
        // Configure load control for smooth buffering
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,        // Min buffer before playback
                /* maxBufferMs = */ 50_000,        // Max buffer size
                /* bufferForPlaybackMs = */ 2_500, // Buffer needed to start
                /* bufferForPlaybackAfterRebufferMs = */ 5_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        
        // Set up caching data source
        val cache = VideoCacheManager.getCache(context)
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(15_000)
        
        val upstreamFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(cacheDataSourceFactory)
        
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f // Start muted
                
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isBuffering = state == Player.STATE_BUFFERING
                    }
                    
                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                        if (playing) {
                            isBuffering = false
                        }
                    }
                })
            }
    }
    
    // Apply mute/unmute
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }
    
    // Apply playback speed
    LaunchedEffect(playbackSpeed) {
        exoPlayer.setPlaybackSpeed(playbackSpeed)
    }

    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                exoPlayer.pause()
            }

            override fun onStop(owner: LifecycleOwner) {
                exoPlayer.pause()
            }

            override fun onResume(owner: LifecycleOwner) {
                exoPlayer.playWhenReady = true
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    controlsVisible = !controlsVisible
                }
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
        
        // Video controls overlay
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause button with proper icon switching
                IconButton(
                    onClick = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                        } else {
                            exoPlayer.play()
                        }
                    }
                ) {
                    Icon(
                        if (exoPlayer.isPlaying) AppIcons.Pause else AppIcons.PlayArrow,
                        contentDescription = if (exoPlayer.isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Mute/Unmute button with proper icon switching
                IconButton(
                    onClick = { isMuted = !isMuted }
                ) {
                    Icon(
                        if (isMuted) AppIcons.VolumeOff else AppIcons.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
