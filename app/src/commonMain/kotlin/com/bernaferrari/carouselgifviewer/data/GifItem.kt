package com.bernaferrari.carouselgifviewer.data

import kotlinx.serialization.Serializable

@Serializable
data class GifItem(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val shareUrl: String = videoUrl,
    val description: String? = null,
    val verseReference: String? = null,
    val verseText: String? = null,
    val isFavorite: Boolean = false
)
