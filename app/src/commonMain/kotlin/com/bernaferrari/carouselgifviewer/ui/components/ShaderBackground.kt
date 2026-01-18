package com.bernaferrari.carouselgifviewer.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.data.ShaderType
import com.bernaferrari.carouselgifviewer.ui.components.shaders.*

@Composable
fun ShaderBackground(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shaderType: ShaderType = ShaderType.Zen
) {
    if (!enabled) return

    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier.applyShader(shaderType, isDark)
    )
}

fun Modifier.applyShader(type: ShaderType, isDark: Boolean): Modifier {
    return when (type) {
        // Basic
        ShaderType.Aurora -> this.auroraEffect(isDark)
        ShaderType.LavaLamp -> this.lavaLampEffect(isDark)
        ShaderType.Waves -> this.wavesEffect(isDark)
        ShaderType.Nebula -> this.nebulaEffect(isDark)
        ShaderType.Candy -> this.candyEffect(isDark)
        ShaderType.Orbs -> this.orbsEffect(isDark)
        ShaderType.Vortex -> this.vortexEffect(isDark)
        ShaderType.Wormhole -> this.wormholeEffect(isDark)
        ShaderType.Silk -> this.silkEffect(isDark)
        ShaderType.Mosaic -> this.mosaicEffect(isDark)
        ShaderType.Plasma -> this.plasmaEffect(isDark)
        ShaderType.Stardust -> this.stardustEffect(isDark)
        ShaderType.Prism -> this.prismEffect(isDark)

        // Alt Versions
        ShaderType.AuroraAlt -> this.auroraCreativeEffect(isDark)
        ShaderType.LavaLampAlt -> this.lavaLampCreativeEffect(isDark)
        ShaderType.NebulaAlt -> this.nebulaCreativeEffect(isDark)
        ShaderType.WavesAlt -> this.wavesCreativeEffect(isDark)
        ShaderType.SilkAlt -> this.silkCreativeEffect(isDark)
        
        // Special
        ShaderType.Dreamy -> this.dreamyEffect(isDark)
        ShaderType.Starlight -> this.starlightEffect(isDark)
        ShaderType.Transit -> this.transitEffect(isDark)
        ShaderType.HexGrid -> this.hexGridEffect(isDark)
        ShaderType.GoldenRings -> this.goldenRingsEffect(isDark)
        ShaderType.Ether -> this.etherEffect(isDark)
        ShaderType.Inferno -> this.infernoEffect(isDark)
        ShaderType.Cinders -> this.cindersEffect(isDark)
        ShaderType.InkBlot -> this.inkBlotEffect(isDark)
        ShaderType.Monochrome -> this.monochromeEffect(isDark)
        ShaderType.Hellfire -> this.hellfireEffect(isDark)
        ShaderType.Scanline -> this.scanlineEffect(isDark)
        ShaderType.Radar -> this.radarEffect(isDark)
        ShaderType.Chrome -> this.chromeEffect(isDark)
        ShaderType.Zen -> this.zenEffect(isDark)
        else -> this
    }
}