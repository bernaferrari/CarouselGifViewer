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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.testTag
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
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
            val databaseProvider = StandaloneDatabaseProvider(context.applicationContext)
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
internal actual fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier,
    playbackSpeed: Float,
    posterUrl: String?,
    playbackEnabled: Boolean,
    preferences: PlaybackPreferences,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val requestedPlaying = preferences.isPlayingRequested
    val currentRequestedPlaying by rememberUpdatedState(requestedPlaying)
    val currentPlaybackEnabled by rememberUpdatedState(playbackEnabled)
    var firstFrameRendered by remember(videoUrl) { mutableStateOf(false) }
    var playbackFailed by remember(videoUrl) { mutableStateOf(false) }
    var isBuffering by remember(videoUrl) { mutableStateOf(true) }
    var showLoading by remember { mutableStateOf(false) }
    var controlsVisible by rememberSaveable(videoUrl) { mutableStateOf(true) }
    val isMuted = preferences.isMuted
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
                seekTo(preferences.positionMillis)
                prepare()
                playWhenReady = requestedPlaying && playbackEnabled && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f // Start muted
                
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isBuffering = state == Player.STATE_BUFFERING
                    }
                    
                    override fun onRenderedFirstFrame() {
                        firstFrameRendered = true
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        playbackFailed = true
                        isBuffering = false
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

    LaunchedEffect(exoPlayer, playbackEnabled, requestedPlaying) {
        exoPlayer.playWhenReady = playbackEnabled && requestedPlaying &&
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    // Apply mute/unmute
    LaunchedEffect(exoPlayer, isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }
    
    // Apply playback speed
    LaunchedEffect(exoPlayer, playbackSpeed) {
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
                exoPlayer.playWhenReady = currentRequestedPlaying && currentPlaybackEnabled
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            preferences.positionMillis = exoPlayer.currentPosition.coerceAtLeast(0L)
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
            update = { view -> view.player = exoPlayer },
            onRelease = { view -> view.player = null },
            modifier = Modifier
                .fillMaxSize()
                .testTag("videoSurface")
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClickLabel = if (controlsVisible) "Hide video controls" else "Show video controls",
                    indication = null
                ) {
                    controlsVisible = !controlsVisible
                }
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
                FilledTonalButton(enabled = playbackEnabled, onClick = {
                    playbackFailed = false
                    isBuffering = true
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = currentRequestedPlaying && currentPlaybackEnabled
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
        
        // Video controls overlay
        AnimatedVisibility(
            visible = controlsVisible && playbackEnabled,
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
                        preferences.isPlayingRequested = !preferences.isPlayingRequested
                        exoPlayer.playWhenReady = preferences.isPlayingRequested && playbackEnabled
                    }
                ) {
                    Icon(
                        if (requestedPlaying) AppIcons.Pause else AppIcons.PlayArrow,
                        contentDescription = if (requestedPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Mute/Unmute button with proper icon switching
                IconButton(
                    onClick = { preferences.isMuted = !preferences.isMuted }
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
