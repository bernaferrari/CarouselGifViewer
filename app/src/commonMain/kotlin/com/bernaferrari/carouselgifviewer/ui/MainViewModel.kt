package com.bernaferrari.carouselgifviewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.carouselgifviewer.data.DownloadManager
import com.bernaferrari.carouselgifviewer.data.DownloadProgress
import com.bernaferrari.carouselgifviewer.data.GifItem
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.data.SettingsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterCategory {
    All,
    Favorites
}

data class MainUiState(
    val items: List<GifItem> = emptyList(),
    val filteredItems: List<GifItem> = emptyList(),
    val filterQuery: String = "",
    val selectedIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentItem: GifItem? = null,
    val activeFilter: FilterCategory = FilterCategory.All,
    val downloadedIds: Set<String> = emptySet(),
    val downloadProgress: Map<String, DownloadProgress> = emptyMap(),
    val playbackSpeed: Float = 1f
)

class MainViewModel(
    private val repository: GifRepository,
    private val settingsRepository: SettingsRepository,
    private val downloadManager: DownloadManager
) : ViewModel() {

    private val _filterQuery = MutableStateFlow("")
    private val _selectedIndex = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private val _activeFilter = MutableStateFlow(FilterCategory.All)

    // Combine download state separately
    private val downloadState = combine(
        downloadManager.downloadedIds,
        downloadManager.downloadProgress
    ) { ids, progress ->
        DownloadState(ids, progress)
    }

    val uiState: StateFlow<MainUiState> = combine(
        repository.items,
        combine(_filterQuery, _selectedIndex, _isLoading, _error, _activeFilter) { query, selectedIndex, isLoading, error, activeFilter ->
            FilterState(query, selectedIndex, isLoading, error, activeFilter)
        },
        downloadState,
        settingsRepository.settings
    ) { items, filterState, dlState, settings ->
        val pattern = filterState.query.normalizeString()
        var filteredItems = if (pattern.isBlank()) {
            items
        } else {
            items.filter { pattern in it.title.normalizeString() }
        }

        filteredItems = when (filterState.activeFilter) {
            FilterCategory.All -> filteredItems
            FilterCategory.Favorites -> filteredItems.filter { it.isFavorite }
        }

        val currentItem = items.getOrNull(filterState.selectedIndex)

        MainUiState(
            items = items,
            filteredItems = filteredItems,
            filterQuery = filterState.query,
            selectedIndex = filterState.selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)),
            isLoading = filterState.isLoading,
            error = filterState.error,
            currentItem = currentItem,
            activeFilter = filterState.activeFilter,
            downloadedIds = dlState.downloadedIds,
            downloadProgress = dlState.downloadProgress,
            playbackSpeed = settings.playbackSpeed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )
    
    private data class FilterState(
        val query: String,
        val selectedIndex: Int,
        val isLoading: Boolean,
        val error: String?,
        val activeFilter: FilterCategory
    )
    
    private data class DownloadState(
        val downloadedIds: Set<String>,
        val downloadProgress: Map<String, DownloadProgress>
    )

    init {
        viewModelScope.launch {
            repository.items.collect {
                _isLoading.value = false
            }
        }

        // Restore last selected item
        viewModelScope.launch {
            val items = repository.items.first { it.isNotEmpty() }
            val lastId = settingsRepository.settings.first().lastSelectedId
            
            if (lastId != null) {
                val index = items.indexOfFirst { it.id == lastId }
                if (index >= 0) {
                    _selectedIndex.value = index
                }
            }
        }

        // Save selected item with debounce
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _selectedIndex
                .debounce(500)
                .collect { index ->
                    val items = repository.items.first()
                    // Allow saving even if list is empty? No, can't get ID.
                    // But if list is loaded:
                    if (items.isNotEmpty()) {
                        val safeIndex = index.coerceIn(0, items.lastIndex)
                        val item = items[safeIndex]
                        settingsRepository.setLastSelectedId(item.id)
                    }
                }
        }
    }

    fun setFilterQuery(query: String) {
        _filterQuery.value = query
    }

    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun setSelectedById(gifId: String) {
        val index = uiState.value.items.indexOfFirst { it.id == gifId }
        if (index >= 0) {
            _selectedIndex.value = index
        }
    }

    fun setActiveFilter(filter: FilterCategory) {
        _activeFilter.value = filter
    }
    
    fun setError(message: String?) {
        _error.value = message
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            settingsRepository.setPlaybackSpeed(speed)
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }
    
    fun downloadCurrentVideo() {
        val item = uiState.value.currentItem ?: return
        if (downloadManager.isDownloaded(item.id) || downloadManager.isDownloading(item.id)) return
        
        viewModelScope.launch {
            downloadManager.downloadVideo(item.id, item.videoUrl, item.thumbnailUrl)
        }
    }
    
    fun cancelDownload(videoId: String) {
        viewModelScope.launch {
            downloadManager.cancelDownload(videoId)
        }
    }
    
    fun deleteDownload(videoId: String) {
        viewModelScope.launch {
            downloadManager.deleteDownload(videoId)
        }
    }
    
    suspend fun getLocalVideoUrl(videoId: String): String? {
        return downloadManager.getLocalVideoUrl(videoId)
    }
}

private fun String.normalizeString() = lowercase()
