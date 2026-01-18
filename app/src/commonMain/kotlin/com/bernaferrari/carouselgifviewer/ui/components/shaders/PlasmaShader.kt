package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 12. Plasma -> "Spiral Vortex" (Original spinning energy spiral)
@Language("AGSL") private const val PLASMA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 p = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.4;
        
        float r = length(p);
        float a = atan(p.y, p.x);
        
        // Spinning spiral arms
        float spiral = sin(a * 5.0 + r * 10.0 - t * 3.0);
        spiral += sin(a * 3.0 - r * 8.0 + t * 2.0) * 0.5;
        spiral = spiral * 0.5 + 0.5;
        
        // Pulsing center glow
        float glow = 0.05 / (r + 0.1);
        glow *= 0.8 + 0.2 * sin(t * 2.0);
        
        // Combine
        float v = spiral * smoothstep(0.8, 0.0, r) + glow;
        
        // Warm energy gradient
        float3 col = mix(
            float3(0.1, 0.0, 0.15),
            float3(0.9, 0.2, 0.4),
            v
        );
        col += float3(1.0, 0.7, 0.3) * glow * 2.0;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.plasmaEffect(isDark: Boolean): Modifier = runtimeShader(PLASMA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}









