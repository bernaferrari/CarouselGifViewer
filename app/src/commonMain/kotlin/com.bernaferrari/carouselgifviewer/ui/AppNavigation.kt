package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
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
    onShareItem: (String, String) -> Unit,
    onOpenBrowser: (String) -> Unit,
    onClearCache: () -> Unit
) {
    val backStack = rememberNavBackStack(navConfig, MainRoute)
    val settings by settingsViewModel.settings.collectAsState()
    
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MainRoute> {
                MainScreen(
                    viewModel = mainViewModel,
                    onShareItem = onShareItem,
                    onOpenBrowser = onOpenBrowser,
                    onOpenSettings = { backStack.add(SettingsRoute) },
                    shaderEffectEnabled = settings.shaderEffectEnabled
                )
            }
            
            entry<SettingsRoute> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { backStack.removeLastOrNull() },
                    onClearCache = onClearCache,
                    onOpenUrl = onOpenBrowser
                )
            }
        }
    )
}
