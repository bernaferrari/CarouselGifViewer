package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 9. Silk -> "Velvet Flow" (Smooth flowing silk ribbons)
@Language("AGSL") private const val SILK_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy * 2.0 - 1.0;
        uv.x *= iResolution.x / iResolution.y;
        float t = iTime * 0.4;
        
        // Smooth flowing distortion
        for(float i = 1.0; i < 5.0; i++) {
            uv.x += 0.45/i * sin(i * 2.8 * uv.y + t);
            uv.y += 0.45/i * cos(i * 2.5 * uv.x + t * 0.9);
        }
        
        // Create flowing silk pattern
        float v = sin(uv.x * 1.5 + uv.y * 1.5);
        v = v * 0.5 + 0.5;
        
        // Rich silk colors - deep magenta to pink to soft lavender
        float3 dark = float3(0.25, 0.02, 0.15);
        float3 mid = float3(0.8, 0.15, 0.45);
        float3 bright = float3(0.95, 0.6, 0.8);
        
        float3 col = mix(dark, mid, smoothstep(0.15, 0.45, v));
        col = mix(col, bright, smoothstep(0.5, 0.85, v));
        
        return half4(col, 1.0);
    }
"""

fun Modifier.silkEffect(isDark: Boolean): Modifier = runtimeShader(SILK_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

