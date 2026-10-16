package com.bernaferrari.carouselgifviewer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.data.ThemeMode
import com.bernaferrari.carouselgifviewer.ui.AppNavigation
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
import com.bernaferrari.carouselgifviewer.ui.SettingsViewModel
import com.bernaferrari.carouselgifviewer.ui.VideoCacheManager
import com.bernaferrari.carouselgifviewer.ui.accentGradients
import com.bernaferrari.carouselgifviewer.ui.theme.CarouselGifViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as MainApplication
            val settingsRepository = app.settingsRepository
            val downloadManager = app.downloadManager
            val models = remember {
                ViewModelProvider(this@MainActivity, viewModelFactory {
                    initializer {
                        MainViewModel(
                            repository = GifRepository(DatabaseDataSource(), settingsRepository),
                            settingsRepository = settingsRepository,
                            downloadManager = downloadManager,
                        )
                    }
                    initializer { SettingsViewModel(settingsRepository) }
                })
            }
            val viewModel = remember { models[MainViewModel::class.java] }
            val settingsViewModel = remember { models[SettingsViewModel::class.java] }
            val settings by settingsViewModel.settings.collectAsState()
            
            val isDarkTheme = when (settings.themeMode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.MaterialYou -> isSystemInDarkTheme()
            }
            
            val useMaterialYou = settings.themeMode == ThemeMode.MaterialYou
            val dynamicColorScheme = if (useMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isDarkTheme) dynamicDarkColorScheme(this@MainActivity)
                else dynamicLightColorScheme(this@MainActivity)
            } else null
            
            val accentColor = if (useMaterialYou) null 
                else accentGradients.getOrNull(settings.accentColorIndex)?.first
            
            CarouselGifViewerTheme(
                darkTheme = isDarkTheme,
                accentColor = accentColor,
                dynamicColorScheme = dynamicColorScheme
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        mainViewModel = viewModel,
                        settingsViewModel = settingsViewModel,
                        downloadManager = downloadManager,
                        onShareItem = { title, url -> shareItem(title, url) },
                        onOpenBrowser = { url -> openBrowser(url) },
                        onClearCache = { clearCache() }
                    )
                }
            }
        }
    }
    
    private fun clearCache() {
        VideoCacheManager.release()
        Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
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
