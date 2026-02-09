package com.bernaferrari.carouselgifviewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val currentItem: GifItem? = null,
    val activeFilter: FilterCategory = FilterCategory.All
)

class MainViewModel(
    private val repository: GifRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _filterQuery = MutableStateFlow("")
    private val _selectedIndex = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(true)
    private val _activeFilter = MutableStateFlow(FilterCategory.All)

    val uiState: StateFlow<MainUiState> = combine(
        repository.items,
        _filterQuery,
        _selectedIndex,
        _isLoading,
        _activeFilter
    ) { items, query, selectedIndex, isLoading, activeFilter ->
        val pattern = query.normalizeString()
        var filteredItems = if (pattern.isBlank()) {
            items
        } else {
            items.filter { pattern in it.title.normalizeString() }
        }

        filteredItems = when (activeFilter) {
            FilterCategory.All -> filteredItems
            FilterCategory.Favorites -> filteredItems.filter { it.isFavorite }
        }

        val currentItem = items.getOrNull(selectedIndex)

        MainUiState(
            items = items,
            filteredItems = filteredItems,
            filterQuery = query,
            selectedIndex = selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)),
            isLoading = isLoading,
            currentItem = currentItem,
            activeFilter = activeFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
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

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }
}

private fun String.normalizeString() =
    lowercase()
