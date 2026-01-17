package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun KmpAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
)

@Composable
expect fun KmpVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
)
