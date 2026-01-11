package com.bernaferrari.carouselgifviewer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium vibrant color palette
object AppColors {
    // Primary - Rich Purple
    val Purple80 = Color(0xFFD0BCFF)
    val Purple60 = Color(0xFFB794F6)
    val Purple40 = Color(0xFF9A67EA)
    val Purple20 = Color(0xFF7C4DFF)
    
    // Secondary - Electric Blue
    val Blue80 = Color(0xFFB4C5FF)
    val Blue60 = Color(0xFF82B1FF)
    val Blue40 = Color(0xFF448AFF)
    val Blue20 = Color(0xFF2979FF)
    
    // Tertiary - Coral Pink
    val Coral80 = Color(0xFFFFB4AB)
    val Coral60 = Color(0xFFFF8A80)
    val Coral40 = Color(0xFFFF5252)
    
    // Surface colors
    val SurfaceDark = Color(0xFF121218)
    val SurfaceContainerDark = Color(0xFF1D1D25)
    val SurfaceContainerHighDark = Color(0xFF282833)
    val SurfaceLight = Color(0xFFFFFBFF)
    val SurfaceContainerLight = Color(0xFFF3EFF4)
    val SurfaceContainerHighLight = Color(0xFFEBE7EC)
    
    // Gradients
    val primaryGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(Purple40, Blue40)
        )
    
    val accentGradient: Brush
        @Composable
        get() = Brush.linearGradient(
            colors = listOf(Purple60, Coral60)
        )
    
    val surfaceGradient: Brush
        @Composable
        get() = if (isSystemInDarkTheme()) {
            Brush.verticalGradient(
                colors = listOf(SurfaceDark, SurfaceContainerDark)
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(SurfaceLight, SurfaceContainerLight)
            )
        }
    
    val glassOverlay: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = if (isSystemInDarkTheme()) {
                listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.04f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.7f),
                    Color.White.copy(alpha = 0.4f)
                )
            }
        )
}

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Purple60,
    onPrimary = Color.White,
    primaryContainer = AppColors.Purple40,
    onPrimaryContainer = Color.White,
    secondary = AppColors.Blue60,
    onSecondary = Color.White,
    secondaryContainer = AppColors.Blue40,
    onSecondaryContainer = Color.White,
    tertiary = AppColors.Coral60,
    onTertiary = Color.White,
    tertiaryContainer = AppColors.Coral40,
    onTertiaryContainer = Color.White,
    background = AppColors.SurfaceDark,
    onBackground = Color.White,
    surface = AppColors.SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = AppColors.SurfaceContainerDark,
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),
    surfaceContainerLowest = Color(0xFF0D0D12),
    surfaceContainerLow = AppColors.SurfaceDark,
    surfaceContainer = AppColors.SurfaceContainerDark,
    surfaceContainerHigh = AppColors.SurfaceContainerHighDark,
    surfaceContainerHighest = Color(0xFF333340)
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Purple40,
    onPrimary = Color.White,
    primaryContainer = AppColors.Purple80,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = AppColors.Blue40,
    onSecondary = Color.White,
    secondaryContainer = AppColors.Blue80,
    onSecondaryContainer = Color(0xFF001849),
    tertiary = AppColors.Coral40,
    onTertiary = Color.White,
    tertiaryContainer = AppColors.Coral80,
    onTertiaryContainer = Color(0xFF410002),
    background = AppColors.SurfaceLight,
    onBackground = Color(0xFF1C1B1F),
    surface = AppColors.SurfaceLight,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = AppColors.SurfaceContainerLight,
    onSurfaceVariant = Color(0xFF49454F),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F2F7),
    surfaceContainer = AppColors.SurfaceContainerLight,
    surfaceContainerHigh = AppColors.SurfaceContainerHighLight,
    surfaceContainerHighest = Color(0xFFE5E1E6)
)

@Composable
fun CarouselGifViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
