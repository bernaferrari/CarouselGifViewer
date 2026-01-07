package com.bernaferrari.carouselgifviewer.data

data class GifItem(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val shareUrl: String = videoUrl
)
