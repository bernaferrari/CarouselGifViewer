package com.bernaferrari.carouselgifviewer.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GifRepository(
    private val dataSource: DictDataSource,
    private val settingsRepository: SettingsRepository
) {
    private val _filterQuery = MutableStateFlow("")

    val items: Flow<List<GifItem>> = combine(
        dataSource.getItems(),
        settingsRepository.favorites
    ) { list, favorites ->
        list.map { item ->
            item.copy(isFavorite = item.id in favorites)
        }
    }

    val filteredItems: Flow<List<GifItem>> = combine(
        items,
        _filterQuery
    ) { list, query ->
        val pattern = query.normalizeString()
        if (pattern.isBlank()) {
            list
        } else {
            list.filter { pattern in it.title.normalizeString() }
        }
    }

    fun setFilterQuery(query: String) {
        _filterQuery.value = query
    }

    fun getItemById(gifId: String): Flow<GifItem?> = items.map { list ->
        list.find { it.id == gifId }
    }

    suspend fun toggleFavorite(id: String) {
        settingsRepository.toggleFavorite(id)
    }
}

private fun String.normalizeString() =
    lowercase()
