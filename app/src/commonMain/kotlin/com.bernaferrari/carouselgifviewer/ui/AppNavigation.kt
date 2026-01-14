package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.runtime.Composable
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
    
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MainRoute> {
                MainScreen(
                    viewModel = mainViewModel,
                    onShareItem = onShareItem,
                    onOpenBrowser = onOpenBrowser,
                    onOpenSettings = { backStack.add(SettingsRoute) }
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
