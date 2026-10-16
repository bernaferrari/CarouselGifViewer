@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlin.experimental.ExperimentalNativeApi::class, kotlinx.cinterop.BetaInteropApi::class)

package com.bernaferrari.carouselgifviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import com.bernaferrari.carouselgifviewer.data.DatabaseDataSource
import com.bernaferrari.carouselgifviewer.data.GifRepository
import com.bernaferrari.carouselgifviewer.data.SettingsRepository
import com.bernaferrari.carouselgifviewer.data.ThemeMode
import com.bernaferrari.carouselgifviewer.data.createDataStore
import com.bernaferrari.carouselgifviewer.data.createDownloadManager
import com.bernaferrari.carouselgifviewer.ui.MainScreen
import com.bernaferrari.carouselgifviewer.ui.MainViewModel
import com.bernaferrari.carouselgifviewer.ui.SettingsScreen
import com.bernaferrari.carouselgifviewer.ui.SettingsViewModel
import com.bernaferrari.carouselgifviewer.ui.accentGradients
import com.bernaferrari.carouselgifviewer.ui.theme.CarouselGifViewerTheme
import kotlinx.cinterop.ObjCAction
import coil3.compose.LocalPlatformContext
import kotlinx.coroutines.launch
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSURL
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIBarButtonItem
import platform.UIKit.*
import platform.UIKit.UIBarButtonItemStyle.UIBarButtonItemStylePlain
import platform.UIKit.UINavigationController
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIViewController
import kotlin.native.ref.WeakReference

// One graph per app, shared by every native destination. In particular, never create
// a second DataStore for the same file when Settings gets pushed again.
private object IosAppGraph {
    val settingsRepository = SettingsRepository(createDataStore())
    val downloadManager = createDownloadManager()
    val mainViewModel = MainViewModel(
        GifRepository(DatabaseDataSource(), settingsRepository),
        settingsRepository,
        downloadManager
    )
    val settingsViewModel = SettingsViewModel(settingsRepository)
}

/** Native bars adopt Liquid Glass with the iOS 26+ SDK, and system styling on older iOS. */
fun MainViewController(): UIViewController = CarouselNavigationController()

private class CarouselNavigationController : UINavigationController(nibName = null, bundle = null) {
    private var openMenu: (() -> Unit)? = null
    private val menuItem by lazy {
        UIBarButtonItem(
            image = menuImage(), style = UIBarButtonItemStylePlain,
            target = this, action = NSSelectorFromString("showMenu")
        ).apply { accessibilityLabel = "Menu" }
    }

    init {
        configureRoot()
    }

    // Keep composable closures outside the Objective-C constructor initializer.
    // The Compose/Native compiler otherwise hoists captured locals incorrectly.
    private fun configureRoot() {
        val navigation = WeakReference(this)
        val root = ComposeUIViewController {
            IosScreenTheme(navigation.get()) {
                val settings by IosAppGraph.settingsViewModel.settings.collectAsState()
                MainScreen(
                    viewModel = IosAppGraph.mainViewModel,
                    onShareItem = { title, url -> navigation.get()?.share(title, url) },
                    onOpenBrowser = ::openBrowser,
                    onOpenSettings = { navigation.get()?.showSettings() },
                    shaderEffectEnabled = settings.shaderEffectEnabled,
                    shaderType = settings.shaderType,
                    nativeTopBar = { title, onOpenMenu ->
                        SideEffect {
                            navigation.get()?.apply {
                                viewControllers.firstOrNull()?.let { controller ->
                                    (controller as UIViewController).apply {
                                        this.title = title.ifEmpty { "Carousel" }
                                        navigationItem.leftBarButtonItem = if (onOpenMenu != null) menuItem else null
                                    }
                                }
                                openMenu = onOpenMenu
                            }
                        }
                    }
                )
            }
        }
        root.edgesForExtendedLayout = 0u
        root.navigationItem.backButtonTitle = "Carousel"
        root.navigationItem.leftBarButtonItem = menuItem
        root.navigationItem.rightBarButtonItem = UIBarButtonItem(
            title = "Settings", style = UIBarButtonItemStylePlain,
            target = this, action = NSSelectorFromString("showSettings")
        )
        setViewControllers(listOf(root), animated = false)
    }

    @ObjCAction
    fun showMenu() {
        if (viewControllers.size == 1 && transitionCoordinator == null) openMenu?.invoke()
    }

    @ObjCAction
    fun showSettings() {
        // Ignore repeated taps and requests during an interactive/cancelled transition.
        if (viewControllers.size != 1 || transitionCoordinator != null) return
        val navigation = WeakReference(this)
        val settings = ComposeUIViewController {
            IosScreenTheme(navigation.get()) {
                val downloadedIds by IosAppGraph.downloadManager.downloadedIds.collectAsState()
                val scope = rememberCoroutineScope()
                val imageLoader = coil3.SingletonImageLoader.get(LocalPlatformContext.current)
                SettingsScreen(
                    viewModel = IosAppGraph.settingsViewModel,
                    onBackClick = { navigation.get()?.popViewControllerAnimated(true) },
                    onClearCache = { imageLoader.memoryCache?.clear() },
                    onClearDownloads = { scope.launch { IosAppGraph.downloadManager.clearAllDownloads() } },
                    onOpenUrl = ::openBrowser,
                    downloadedCount = downloadedIds.size,
                    showTopBar = false
                )
            }
        }
        settings.title = "Settings"
        settings.edgesForExtendedLayout = 0u
        pushViewController(settings, animated = true)
    }

    private fun share(title: String, url: String) {
        val presenter = topViewController ?: return
        if (presenter.presentedViewController != null || transitionCoordinator != null) return
        val sheet = UIActivityViewController(listOf(title, NSURL.URLWithString(url) ?: url), null)
        // UIKit requires an anchor for the activity popover on iPad.
        sheet.popoverPresentationController?.apply {
            sourceView = presenter.view
            sourceRect = presenter.view.bounds
        }
        presenter.presentViewController(sheet, animated = true, completion = null)
    }
}

@Composable
private fun IosScreenTheme(controller: UIViewController?, content: @Composable () -> Unit) {
    val settings by IosAppGraph.settingsViewModel.settings.collectAsState()
    val darkTheme = when (settings.themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        else -> isSystemInDarkTheme()
    }
    SideEffect {
        controller?.overrideUserInterfaceStyle = when (settings.themeMode) {
            ThemeMode.Light -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            ThemeMode.Dark -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
            else -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
        }
    }
    CarouselGifViewerTheme(
        darkTheme = darkTheme,
        accentColor = accentGradients.getOrNull(settings.accentColorIndex)?.first,
    ) {
        val tint = MaterialTheme.colorScheme.primary
        val surface = MaterialTheme.colorScheme.surface
        SideEffect {
            controller?.view?.backgroundColor = UIColor(
                red = surface.red.toDouble(), green = surface.green.toDouble(),
                blue = surface.blue.toDouble(), alpha = surface.alpha.toDouble()
            )
            controller?.view?.tintColor = UIColor(
                red = tint.red.toDouble(), green = tint.green.toDouble(),
                blue = tint.blue.toDouble(), alpha = tint.alpha.toDouble()
            )
        }
        content()
    }
}

private fun openBrowser(url: String) {
    val destination = NSURL.URLWithString(url) ?: return
    if (destination.scheme !in listOf("https", "http")) return
    UIApplication.sharedApplication.openURL(destination, options = emptyMap<Any?, Any>(), completionHandler = null)
}


/** Local path geometry keeps navigation icons independent of icon libraries. */
private fun menuImage(): UIImage = UIGraphicsImageRenderer(size = CGSizeMake(24.0, 24.0))
    .imageWithActions {
        UIColor.blackColor.setStroke()
        val path = UIBezierPath.bezierPath()
        path.lineWidth = 2.0
        for (y in listOf(6.0, 12.0, 18.0)) {
            path.moveToPoint(CGPointMake(3.0, y))
            path.addLineToPoint(CGPointMake(21.0, y))
        }
        path.stroke()
    }.imageWithRenderingMode(UIImageRenderingMode.UIImageRenderingModeAlwaysTemplate)
