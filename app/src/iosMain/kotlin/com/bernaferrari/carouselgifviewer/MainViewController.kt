package com.bernaferrari.carouselgifviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.UIKitViewController
import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.ui.MainScreen
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
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
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        MainScreen(
            viewModel = viewModel,
            onShareItem = { title, url ->
                shareItem(title, url)
            },
            onOpenBrowser = { url ->
                openBrowser(url)
            }
        )
    }
}

private fun shareItem(text: String, content: String) {
    println("Share: $text - $content")
}

private fun openBrowser(url: String) {
    println("Open browser: $url")
}
