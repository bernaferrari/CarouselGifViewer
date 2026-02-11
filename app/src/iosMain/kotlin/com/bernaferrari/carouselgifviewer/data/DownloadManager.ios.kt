package com.bernaferrari.carouselgifviewer.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL

@OptIn(ExperimentalForeignApi::class)
class IosDownloadManager : DownloadManager {
    
    private val documentsDir: String
    private val downloadDir: String
    private val videoDir: String
    private val thumbnailDir: String
    
    private val _downloadProgress = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    override val downloadProgress: StateFlow<Map<String, DownloadProgress>> = _downloadProgress.asStateFlow()
    
    private val _downloadedIds = MutableStateFlow<Set<String>>(emptySet())
    override val downloadedIds: StateFlow<Set<String>> = _downloadedIds.asStateFlow()
    
    init {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        documentsDir = paths.firstOrNull() as? String ?: ""
        downloadDir = "$documentsDir/downloads"
        videoDir = "$downloadDir/videos"
        thumbnailDir = "$downloadDir/thumbnails"
        
        NSFileManager.defaultManager.createDirectoryAtPath(downloadDir, true, null, null)
        NSFileManager.defaultManager.createDirectoryAtPath(videoDir, true, null, null)
        NSFileManager.defaultManager.createDirectoryAtPath(thumbnailDir, true, null, null)
        
        loadDownloadedIds()
    }
    
    private fun loadDownloadedIds() {
        val fileManager = NSFileManager.defaultManager
        val files = fileManager.contentsOfDirectoryAtPath(videoDir, null)
        val ids = files?.filterIsInstance<String>()
            ?.filter { it.endsWith(".mp4") }
            ?.map { it.removeSuffix(".mp4") }
            ?.toSet() ?: emptySet()
        _downloadedIds.value = ids
    }
    
    override suspend fun downloadVideo(videoId: String, videoUrl: String, thumbnailUrl: String) {
        if (isDownloaded(videoId)) return
        
        _downloadProgress.update { map ->
            map + (videoId to DownloadProgress(videoId, 0f, false))
        }
        
        try {
            // Download thumbnail
            val thumbnailUrlObj = NSURL.URLWithString(thumbnailUrl)!!
            val thumbnailData = NSData.dataWithContentsOfURL(thumbnailUrlObj)
            thumbnailData?.writeToURL(NSURL.fileURLWithPath("$thumbnailDir/$videoId.jpg"), true)
            
            _downloadProgress.update { map ->
                map + (videoId to DownloadProgress(videoId, 0.1f, false))
            }
            
            // Download video
            val videoUrlObj = NSURL.URLWithString(videoUrl)!!
            val videoData = NSData.dataWithContentsOfURL(videoUrlObj)
            videoData?.writeToURL(NSURL.fileURLWithPath("$videoDir/$videoId.mp4"), true)
            
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
    
    override suspend fun cancelDownload(videoId: String) {
        NSFileManager.defaultManager.removeItemAtPath("$videoDir/$videoId.mp4", null)
        NSFileManager.defaultManager.removeItemAtPath("$thumbnailDir/$videoId.jpg", null)
        _downloadProgress.update { it - videoId }
    }
    
    override suspend fun deleteDownload(videoId: String) {
        NSFileManager.defaultManager.removeItemAtPath("$videoDir/$videoId.mp4", null)
        NSFileManager.defaultManager.removeItemAtPath("$thumbnailDir/$videoId.jpg", null)
        _downloadedIds.update { it - videoId }
    }
    
    override suspend fun getLocalVideoUrl(videoId: String): String? {
        val path = "$videoDir/$videoId.mp4"
        return if (NSFileManager.defaultManager.fileExistsAtPath(path)) "file://$path" else null
    }
    
    override suspend fun getLocalThumbnailUrl(videoId: String): String? {
        val path = "$thumbnailDir/$videoId.jpg"
        return if (NSFileManager.defaultManager.fileExistsAtPath(path)) "file://$path" else null
    }
    
    override suspend fun clearAllDownloads() {
        NSFileManager.defaultManager.removeItemAtPath(downloadDir, null)
        NSFileManager.defaultManager.createDirectoryAtPath(downloadDir, true, null, null)
        NSFileManager.defaultManager.createDirectoryAtPath(videoDir, true, null, null)
        NSFileManager.defaultManager.createDirectoryAtPath(thumbnailDir, true, null, null)
        _downloadedIds.value = emptySet()
        _downloadProgress.value = emptyMap()
    }
    
    override suspend fun getDownloadedSizeBytes(): Long {
        val fileManager = NSFileManager.defaultManager
        var totalSize: Long = 0
        
        listOf(videoDir, thumbnailDir).forEach { dir ->
            val files = fileManager.contentsOfDirectoryAtPath(dir, null)
            files?.filterIsInstance<String>()?.forEach { fileName ->
                val attributes = fileManager.attributesOfItemAtPath("$dir/$fileName", null)
                val size = attributes?.get("NSFileSize") as? Long ?: 0L
                totalSize += size
            }
        }
        
        return totalSize
    }
    
    override fun isDownloaded(videoId: String): Boolean = videoId in _downloadedIds.value
    
    override fun isDownloading(videoId: String): Boolean =
        videoId in _downloadProgress.value && !_downloadProgress.value[videoId]!!.isComplete
}

fun createDownloadManager(): DownloadManager = IosDownloadManager()
