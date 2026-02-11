package com.bernaferrari.carouselgifviewer.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class DownloadProgress(
    val videoId: String,
    val progress: Float, // 0.0 to 1.0
    val isComplete: Boolean,
    val error: String? = null
)

interface DownloadManager {
    val downloadProgress: StateFlow<Map<String, DownloadProgress>>
    val downloadedIds: StateFlow<Set<String>>
    
    suspend fun downloadVideo(videoId: String, videoUrl: String, thumbnailUrl: String)
    suspend fun cancelDownload(videoId: String)
    suspend fun deleteDownload(videoId: String)
    suspend fun getLocalVideoUrl(videoId: String): String?
    suspend fun getLocalThumbnailUrl(videoId: String): String?
    suspend fun clearAllDownloads()
    suspend fun getDownloadedSizeBytes(): Long
    fun isDownloaded(videoId: String): Boolean
    fun isDownloading(videoId: String): Boolean
}
