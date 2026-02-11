package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.bernaferrari.carouselgifviewer.data.DownloadManager
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data object MainRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(MainRoute::class, MainRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
        }
    }
}

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    downloadManager: DownloadManager,
    onShareItem: (String, String) -> Unit,
    onOpenBrowser: (String) -> Unit,
    onClearCache: () -> Unit
) {
    val backStack = rememberNavBackStack(navConfig, MainRoute)
    val settings by settingsViewModel.settings.collectAsState()
    val downloadedIds by downloadManager.downloadedIds.collectAsState()
    val scope = rememberCoroutineScope()
    
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MainRoute> {
                MainScreen(
                    viewModel = mainViewModel,
                    onShareItem = onShareItem,
                    onOpenBrowser = onOpenBrowser,
                    onOpenSettings = { backStack += SettingsRoute },
                    shaderEffectEnabled = settings.shaderEffectEnabled,
                    shaderType = settings.shaderType
                )
            }
            
            entry<SettingsRoute> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    onClearCache = onClearCache,
                    onClearDownloads = { 
                        scope.launch {
                            downloadManager.clearAllDownloads()
                        }
                    },
                    onOpenUrl = onOpenBrowser,
                    downloadedCount = downloadedIds.size
                )
            }
        }
    )
}

