package com.bernaferrari.carouselgifviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.ui.MainScreen
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
import com.bernaferrari.carouselgifviewer.ui.theme.CarouselGifViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = remember {
                MainViewModel(GifRepository(DatabaseDataSource()))
            }
            CarouselGifViewerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
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
        }
    }

    private fun shareItem(text: String, content: String) {
        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, content)
            putExtra(android.content.Intent.EXTRA_SUBJECT, text)
        }
        startActivity(android.content.Intent.createChooser(shareIntent, "Share with"))
    }

    private fun openBrowser(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }
}
