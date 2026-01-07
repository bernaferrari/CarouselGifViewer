package com.bernaferrari.carouselgifviewer.data

import kotlinx.coroutines.flow.Flow

interface DictDataSource {
    fun getItems(): Flow<List<GifItem>>
}
