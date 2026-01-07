package com.bernaferrari.carouselgifviewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.carouselgifviewer.data.GifItem
import com.bernaferrari.carouselgifviewer.data.GifRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val items: List<GifItem> = emptyList(),
    val filteredItems: List<GifItem> = emptyList(),
    val filterQuery: String = "",
    val selectedIndex: Int = 0,
    val isLoading: Boolean = true,
    val currentItem: GifItem? = null
)

class MainViewModel(
    private val repository: GifRepository
) : ViewModel() {

    private val _filterQuery = MutableStateFlow("")
    private val _selectedIndex = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(true)

    val uiState: StateFlow<MainUiState> = combine(
        repository.items,
        _filterQuery,
        _selectedIndex,
        _isLoading
    ) { items, query, selectedIndex, isLoading ->
        val pattern = query.normalizeString()
        val filteredItems = if (pattern.isBlank()) {
            items
        } else {
            items.filter { pattern in it.title.normalizeString() }
        }
        val currentItem = items.getOrNull(selectedIndex)

        MainUiState(
            items = items,
            filteredItems = filteredItems,
            filterQuery = query,
            selectedIndex = selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)),
            isLoading = isLoading,
            currentItem = currentItem
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
}

private fun String.normalizeString() =
    lowercase()
