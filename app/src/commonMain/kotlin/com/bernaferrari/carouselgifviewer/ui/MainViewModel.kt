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

/**
 * Represents a category filter - either a system filter (All/Favorites) or a data category
 */
data class CategoryFilter(
    val id: String,
    val displayName: String,
    val isSystemFilter: Boolean = true
) {
    companion object {
        val All = CategoryFilter("all", "All", true)
        val Favorites = CategoryFilter("favorites", "Favorites", true)
    }
    
    fun matches(item: GifItem): Boolean {
        return when (id) {
            "all" -> true
            "favorites" -> item.isFavorite
            else -> item.category == id
        }
    }
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
    val activeCategoryFilter: CategoryFilter = CategoryFilter.All,
    val availableCategories: List<CategoryFilter> = emptyList(),
    val downloadedIds: Set<String> = emptySet(),
    val downloadProgress: Map<String, DownloadProgress> = emptyMap(),
    val playbackSpeed: Float = 1f
)

class MainViewModel(
    private val repository: GifRepository,
    private val settingsRepository: SettingsRepository,
    private val downloadManager: DownloadManager
) : ViewModel() {

    private var playbackItemId: String? = null
    private var activePlaybackPreferences = PlaybackPreferences()

    internal fun playbackPreferences(videoId: String): PlaybackPreferences {
        if (playbackItemId != videoId) {
            playbackItemId = videoId
            activePlaybackPreferences = PlaybackPreferences()
        }
        return activePlaybackPreferences
    }

    private val _filterQuery = MutableStateFlow("")
    private val _selectedIndex = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private val _activeFilter = MutableStateFlow(FilterCategory.All)
    private val _activeCategoryFilter = MutableStateFlow<CategoryFilter>(CategoryFilter.All)

    // Combine download state separately
    private val downloadState = combine(
        downloadManager.downloadedIds,
        downloadManager.downloadProgress
    ) { ids, progress ->
        DownloadState(ids, progress)
    }
    
    // Combine filter state separately (to avoid combine overload limits)
    private val filterState = combine(
        _filterQuery,
        _selectedIndex,
        _isLoading,
        _error,
        _activeFilter
    ) { query, selectedIndex, isLoading, error, activeFilter ->
        BasicFilterState(query, selectedIndex, isLoading, error, activeFilter)
    }

    val uiState: StateFlow<MainUiState> = combine(
        repository.items,
        filterState,
        _activeCategoryFilter,
        downloadState,
        settingsRepository.settings
    ) { items, basicFilter, activeCategoryFilter, dlState, settings ->
        val pattern = basicFilter.query.normalizeString()
        var filteredItems = if (pattern.isBlank()) {
            items
        } else {
            items.filter { pattern in it.title.normalizeString() }
        }

        // Apply favorites filter (legacy)
        filteredItems = when (basicFilter.activeFilter) {
            FilterCategory.All -> filteredItems
            FilterCategory.Favorites -> filteredItems.filter { it.isFavorite }
        }
        
        // Apply category filter
        filteredItems = filteredItems.filter { activeCategoryFilter.matches(it) }

        // Extract available categories from items
        val availableCategories = buildList {
            add(CategoryFilter.All)
            add(CategoryFilter.Favorites)
            items.mapNotNull { it.category }
                .distinct()
                .sorted()
                .forEach { category ->
                    add(CategoryFilter(category, category, isSystemFilter = false))
                }
        }

        val safeSelectedIndex = basicFilter.selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
        val currentItem = items.getOrNull(safeSelectedIndex)

        MainUiState(
            items = items,
            filteredItems = filteredItems,
            filterQuery = basicFilter.query,
            selectedIndex = safeSelectedIndex,
            isLoading = basicFilter.isLoading,
            error = basicFilter.error,
            currentItem = currentItem,
            activeFilter = basicFilter.activeFilter,
            activeCategoryFilter = activeCategoryFilter,
            availableCategories = availableCategories,
            downloadedIds = dlState.downloadedIds,
            downloadProgress = dlState.downloadProgress,
            playbackSpeed = settings.playbackSpeed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )
    
    private data class BasicFilterState(
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
        // Save selected item with debounce
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            // Finish restoring before either the UI or persistence can commit the default index.
            val initialItems = repository.items.first()
            val lastId = settingsRepository.settings.first().lastSelectedId
            val restoredIndex = initialItems.indexOfFirst { it.id == lastId }
            _selectedIndex.value = restoredIndex.coerceAtLeast(0)
            _isLoading.value = false
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
    
    fun setCategoryFilter(filter: CategoryFilter) {
        _activeCategoryFilter.value = filter
        // Also sync the legacy filter for backwards compatibility
        _activeFilter.value = when {
            filter.id == "all" -> FilterCategory.All
            filter.id == "favorites" -> FilterCategory.Favorites
            else -> FilterCategory.All
        }
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
