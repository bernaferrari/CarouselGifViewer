package com.bernaferrari.carouselgifviewer.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class AndroidDownloadManager(context: Context) : DownloadManager {
    
    private val downloadDir = File(context.filesDir, "downloads").apply { mkdirs() }
    private val thumbnailDir = File(downloadDir, "thumbnails").apply { mkdirs() }
    private val videoDir = File(downloadDir, "videos").apply { mkdirs() }
    
    private val _downloadProgress = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    override val downloadProgress: StateFlow<Map<String, DownloadProgress>> = _downloadProgress.asStateFlow()
    
    private val _downloadedIds = MutableStateFlow<Set<String>>(emptySet())
    override val downloadedIds: StateFlow<Set<String>> = _downloadedIds.asStateFlow()
    
    private val activeDownloads = mutableMapOf<String, Job>()
    
    init {
        loadDownloadedIds()
    }
    
    private fun loadDownloadedIds() {
        val existingIds = videoDir.listFiles()
            ?.filter { it.extension == "mp4" }
            ?.map { it.nameWithoutExtension }
            ?.toSet() ?: emptySet()
        _downloadedIds.value = existingIds
    }
    
    override suspend fun downloadVideo(videoId: String, videoUrl: String, thumbnailUrl: String) {
        if (isDownloaded(videoId)) return
        
        _downloadProgress.update { map ->
            map + (videoId to DownloadProgress(videoId, 0f, false))
        }
        
        try {
            // Download thumbnail first (small, quick)
            downloadFile(thumbnailUrl, File(thumbnailDir, "$videoId.jpg")) { progress ->
                _downloadProgress.update { map ->
                    map + (videoId to DownloadProgress(videoId, progress * 0.1f, false))
                }
            }
            
            // Download video
            downloadFile(videoUrl, File(videoDir, "$videoId.mp4")) { progress ->
                _downloadProgress.update { map ->
                    map + (videoId to DownloadProgress(videoId, 0.1f + progress * 0.9f, false))
                }
            }
            
            _downloadProgress.update { map ->
                map + (videoId to DownloadProgress(videoId, 1f, true))
            }
            _downloadedIds.update { it + videoId }
        } catch (e: Exception) {
            _downloadProgress.update { map ->
                map + (videoId to DownloadProgress(videoId, 0f, false, e.message))
            }
            throw e
        }
    }
    
    private suspend fun downloadFile(
        url: String,
        destination: File,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection()
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        
        val totalBytes = connection.contentLengthLong
        var downloadedBytes = 0L
        
        connection.getInputStream().use { input ->
            destination.outputStream().use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    if (!isActive) {
                        destination.delete()
                        throw kotlinx.coroutines.CancellationException("Download cancelled")
                    }
                    output.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead
                    
                    if (totalBytes > 0) {
                        onProgress(downloadedBytes.toFloat() / totalBytes)
                    }
                }
            }
        }
    }
    
    override suspend fun cancelDownload(videoId: String) {
        activeDownloads[videoId]?.cancel()
        activeDownloads.remove(videoId)
        
        File(videoDir, "$videoId.mp4").delete()
        File(thumbnailDir, "$videoId.jpg").delete()
        
        _downloadProgress.update { it - videoId }
    }
    
    override suspend fun deleteDownload(videoId: String) {
        File(videoDir, "$videoId.mp4").delete()
        File(thumbnailDir, "$videoId.jpg").delete()
        _downloadedIds.update { it - videoId }
    }
    
    override suspend fun getLocalVideoUrl(videoId: String): String? {
        val file = File(videoDir, "$videoId.mp4")
        return if (file.exists()) file.absolutePath else null
    }
    
    override suspend fun getLocalThumbnailUrl(videoId: String): String? {
        val file = File(thumbnailDir, "$videoId.jpg")
        return if (file.exists()) file.absolutePath else null
    }
    
    override suspend fun clearAllDownloads() {
        videoDir.deleteRecursively()
        thumbnailDir.deleteRecursively()
        videoDir.mkdirs()
        thumbnailDir.mkdirs()
        _downloadedIds.value = emptySet()
        _downloadProgress.value = emptyMap()
    }
    
    override suspend fun getDownloadedSizeBytes(): Long {
        return videoDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum() +
               thumbnailDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }
    
    override fun isDownloaded(videoId: String): Boolean = videoId in _downloadedIds.value
    
    override fun isDownloading(videoId: String): Boolean =
        videoId in _downloadProgress.value && !_downloadProgress.value[videoId]!!.isComplete
}

fun createDownloadManager(context: Context): DownloadManager = AndroidDownloadManager(context)
