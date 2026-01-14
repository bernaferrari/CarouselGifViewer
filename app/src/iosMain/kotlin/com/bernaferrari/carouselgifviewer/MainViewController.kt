package com.bernaferrari.carouselgifviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.interop.UIKitViewController
import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.ui.MainScreen
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
import com.bernaferrari.carouselgifviewer.ui.SettingsScreen
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return UIKitViewController {
        CarouselGifViewerApp()
    }
}

@Composable
fun CarouselGifViewerApp() {
    val viewModel = remember {
        MainViewModel(GifRepository(DatabaseDataSource()))
    }
    var showSettings by remember { mutableStateOf(false) }
    
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        if (showSettings) {
            SettingsScreen(
                onBackClick = { showSettings = false },
                onClearCache = { /* iOS cache clear */ },
                onOpenUrl = { url -> openBrowser(url) }
            )
        } else {
            MainScreen(
                viewModel = viewModel,
                onShareItem = { title, url ->
                    shareItem(title, url)
                },
                onOpenBrowser = { url ->
                    openBrowser(url)
                },
                onOpenSettings = { showSettings = true }
            )
        }
    }
}

private fun shareItem(text: String, content: String) {
    println("Share: $text - $content")
}

private fun openBrowser(url: String) {
    println("Open browser: $url")
}
