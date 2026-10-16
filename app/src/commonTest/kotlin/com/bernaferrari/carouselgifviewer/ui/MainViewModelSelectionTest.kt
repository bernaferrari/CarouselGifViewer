package com.bernaferrari.carouselgifviewer.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelStore
import com.bernaferrari.carouselgifviewer.data.DictDataSource
import com.bernaferrari.carouselgifviewer.data.DownloadManager
import com.bernaferrari.carouselgifviewer.data.DownloadProgress
import com.bernaferrari.carouselgifviewer.data.GifItem
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModelSelectionTest {
    @Test fun restoringSelectionCannotPersistTheDefaultOverTheSavedItem() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val preferences = MemoryPreferences(mutablePreferencesOf(stringPreferencesKey("last_selected_id") to "b"))
            val settings = SettingsRepository(preferences)
            val source = Source(listOf(item("a"), item("b")))
            val vm = MainViewModel(GifRepository(source, settings), settings, NoDownloads)
            store.put("test", vm)
            backgroundScope.launch { vm.uiState.collect() }
            runCurrent()
            assertFalse(vm.uiState.value.isLoading)
            assertEquals(1, vm.uiState.value.selectedIndex)
            assertEquals("b", vm.uiState.value.currentItem?.id)
            advanceTimeBy(600)
            runCurrent()
            assertEquals("b", settings.settings.first().lastSelectedId)
            vm.setSelectedIndex(999)
            runCurrent()
            assertEquals(1, vm.uiState.value.selectedIndex)
            assertEquals("b", vm.uiState.value.currentItem?.id)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    @Test fun emptyLibrariesFinishLoadingWithoutAnInvalidSelection() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val settings = SettingsRepository(MemoryPreferences())
            val vm = MainViewModel(GifRepository(Source(emptyList()), settings), settings, NoDownloads)
            store.put("test", vm)
            backgroundScope.launch { vm.uiState.collect() }
            runCurrent()
            assertFalse(vm.uiState.value.isLoading)
            assertEquals(0, vm.uiState.value.selectedIndex)
            assertNull(vm.uiState.value.currentItem)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    private fun item(id: String) = GifItem(id, id, "https://example.com/$id.mp4", "https://example.com/$id.jpg")
    private class Source(items: List<GifItem>) : DictDataSource {
        val items = MutableStateFlow(items)
        override fun getItems() = items
    }
    private class MemoryPreferences(initial: Preferences = emptyPreferences()) : DataStore<Preferences> {
        override val data = MutableStateFlow(initial)
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
            transform(data.value).also { data.value = it }
    }
    private object NoDownloads : DownloadManager {
        override val downloadProgress = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
        override val downloadedIds = MutableStateFlow<Set<String>>(emptySet())
        override suspend fun downloadVideo(videoId: String, videoUrl: String, thumbnailUrl: String) = Unit
        override suspend fun cancelDownload(videoId: String) = Unit
        override suspend fun deleteDownload(videoId: String) = Unit
        override suspend fun getLocalVideoUrl(videoId: String): String? = null
        override suspend fun getLocalThumbnailUrl(videoId: String): String? = null
        override suspend fun clearAllDownloads() = Unit
        override suspend fun getDownloadedSizeBytes() = 0L
        override fun isDownloaded(videoId: String) = false
        override fun isDownloading(videoId: String) = false
    }
}
