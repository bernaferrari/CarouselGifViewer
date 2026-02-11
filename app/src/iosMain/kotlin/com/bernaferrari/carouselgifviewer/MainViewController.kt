package com.bernaferrari.carouselgifviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.data.SettingsRepository
import com.bernaferrari.carouselgifviewer.data.ThemeMode
import com.bernaferrari.carouselgifviewer.data.createDataStore
import com.bernaferrari.carouselgifviewer.data.createDownloadManager
import com.bernaferrari.carouselgifviewer.ui.AppNavigation
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
import com.bernaferrari.carouselgifviewer.ui.SettingsViewModel
import com.bernaferrari.carouselgifviewer.ui.accentGradients
import com.bernaferrari.carouselgifviewer.ui.theme.CarouselGifViewerTheme
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    CarouselGifViewerApp()
}

@Composable
fun CarouselGifViewerApp() {
    val settingsRepository = remember { SettingsRepository(createDataStore()) }
    val downloadManager = remember { createDownloadManager() }
    val viewModel = remember {
        MainViewModel(
            repository = GifRepository(DatabaseDataSource(), settingsRepository),
            settingsRepository = settingsRepository,
            downloadManager = downloadManager
        )
    }
    val settingsViewModel = remember {
        SettingsViewModel(settingsRepository)
    }
    val settings by settingsViewModel.settings.collectAsState()
    
    val isDarkTheme = when (settings.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.MaterialYou -> isSystemInDarkTheme()
    }
    val accentColor = accentGradients.getOrNull(settings.accentColorIndex)?.first
    
    CarouselGifViewerTheme(
        darkTheme = isDarkTheme,
        accentColor = accentColor
    ) {
        AppNavigation(
            mainViewModel = viewModel,
            settingsViewModel = settingsViewModel,
            downloadManager = downloadManager,
            onShareItem = { title, url -> shareItem(title, url) },
            onOpenBrowser = { url -> openBrowser(url) },
            onClearCache = { /* iOS cache clear */ }
        )
    }
}

private fun shareItem(text: String, content: String) {
    println("Share: $text - $content")
}

private fun openBrowser(url: String) {
    println("Open browser: $url")
}
