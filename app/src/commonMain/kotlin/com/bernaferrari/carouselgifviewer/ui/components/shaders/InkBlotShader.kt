package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Toon -> "Ink Flow" (Elegant flowing ink waves)
@Language("AGSL") private const val TOON_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy * 2.0 - 1.0;
        uv.x *= iResolution.x / iResolution.y;
        float t = iTime * 0.3;
        
        // Flowing wave distortion (like Silk but different formula)
        for(float i = 1.0; i < 6.0; i++) {
            uv.x += 0.4/i * sin(i * 2.5 * uv.y + t * 1.2);
            uv.y += 0.4/i * cos(i * 2.0 * uv.x + t);
        }
        
        // Create flowing pattern
        float v = sin(uv.x * 2.0) * cos(uv.y * 2.0);
        v = v * 0.5 + 0.5;
        
        // Warm ink colors - cream to sepia to dark brown  
        float3 light = float3(0.95, 0.9, 0.8);
        float3 mid = float3(0.6, 0.4, 0.25);
        float3 dark = float3(0.15, 0.1, 0.08);
        
        float3 col = mix(dark, mid, smoothstep(0.0, 0.5, v));
        col = mix(col, light, smoothstep(0.5, 1.0, v));
        
        return half4(col, 1.0);
    }
"""

fun Modifier.inkBlotEffect(isDark: Boolean): Modifier = runtimeShader(TOON_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}




