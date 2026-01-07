package com.bernaferrari.carouselgifviewer.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class GifRepository(
    private val dataSource: DictDataSource
) {
    private val _filterQuery = MutableStateFlow("")

    val items: Flow<List<GifItem>> = dataSource.getItems()

    val filteredItems: Flow<List<GifItem>> = items.map { list ->
        val pattern = _filterQuery.value.normalizeString()
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
}

private fun String.normalizeString() =
    lowercase()
