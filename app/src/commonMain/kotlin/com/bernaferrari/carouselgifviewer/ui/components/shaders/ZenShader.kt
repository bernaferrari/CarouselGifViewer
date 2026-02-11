package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Zen (Breathe)
private const val ZEN_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime;
        
        // Soft gradient background
        half3 col = mix(half3(0.8, 0.9, 1.0), half3(0.9, 0.8, 0.9), uv.y + 0.5);
        
        // Breathing Circle
        float breath = sin(t * 0.5) * 0.5 + 0.5; // 0 to 1 slow
        float radius = 0.2 + 0.1 * breath;
        
        float d = length(uv);
        float circle = smoothstep(radius, radius - 0.01, d);
        
        // Bloom
        float bloom = exp(-d * 2.0) * breath;
        
        col += half3(0.4, 0.6, 0.8) * circle * 0.5;
        col += half3(1.0, 1.0, 1.0) * bloom * 0.2;

        return half4(col, 1.0);
    }
"""

fun Modifier.zenEffect(isDark: Boolean): Modifier = runtimeShader(ZEN_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
