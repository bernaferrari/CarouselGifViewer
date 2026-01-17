package com.bernaferrari.carouselgifviewer.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.data.ShaderType

@Composable
fun ShaderBackground(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shaderType: ShaderType = ShaderType.Aurora
) {
    if (!enabled) return

    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier.applyShader(shaderType, isDark)
    )
}

private fun Modifier.applyShader(type: ShaderType, isDark: Boolean): Modifier {
    return when (type) {
        ShaderType.Aurora -> this.auroraEffect(isDark)
        ShaderType.LavaLamp -> this.lavaLampEffect(isDark)
        ShaderType.Waves -> this.wavesEffect(isDark)
        ShaderType.Nebula -> this.nebulaEffect(isDark)
        ShaderType.Candy -> this.candyEffect(isDark)
        ShaderType.Juggling -> this.jugglingEffect(isDark)
        ShaderType.Vortex -> this.vortexEffect(isDark)
        ShaderType.Vortex2 -> this.vortex2Effect(isDark)
        ShaderType.Silk -> this.silkEffect(isDark)
        ShaderType.Mosaic -> this.mosaicEffect(isDark)
        ShaderType.Cyber -> this.cyberEffect(isDark)
        ShaderType.Plasma -> this.plasmaEffect(isDark)
        ShaderType.Fireflies -> this.firefliesEffect(isDark)
        ShaderType.Matrix -> this.matrixEffect(isDark)
        ShaderType.DNA -> this.dnaEffect(isDark)
        ShaderType.BlackHole -> this.blackHoleEffect(isDark)
        ShaderType.Topography -> this.topographyEffect(isDark)
        ShaderType.Cosmic -> this.cosmicEffect(isDark)
        else -> this // Fallback for unsupported shaders like LiquidGlass, Crystal, MaterialYou
    }
}