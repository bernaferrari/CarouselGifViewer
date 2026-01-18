package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 19. LiquidGlass -> "Chromatic Distortion" (Prism)
@Language("AGSL") private const val LIQUID_GLASS_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float t = iTime;
        
        // Base pattern (Checkerboard/Texture to distort)
        // We'll generate a colorful pattern since we don't have a texture input
        half3 col = half3(0.0);
        
        // Heavy Distortion map
        float2 distort = float2(
            sin(uv.y * 5.0 + t) * sin(uv.x * 5.0 + t*0.5),
            cos(uv.x * 5.0 - t) * cos(uv.y * 5.0 - t*0.5)
        ) * 0.1;
        
        // Chromatic Aberration separation
        float2 rUV = uv + distort;
        float2 gUV = uv + distort * 1.05;
        float2 bUV = uv + distort * 1.1;
        
        float rVal = sin(rUV.x * 20.0) + sin(rUV.y * 20.0);
        float gVal = sin(gUV.x * 20.0) + sin(gUV.y * 20.0);
        float bVal = sin(bUV.x * 20.0) + sin(bUV.y * 20.0);
        
        col.r = smoothstep(0.0, 0.1, abs(rVal));
        col.g = smoothstep(0.0, 0.1, abs(gVal));
        col.b = smoothstep(0.0, 0.1, abs(bVal));
        
        // Glass shine
        float shine = sin(uv.x * 10.0 + uv.y * 10.0 + t * 2.0);
        col += half3(1.0) * smoothstep(0.9, 1.0, shine) * 0.5;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.prismEffect(isDark: Boolean): Modifier = runtimeShader(LIQUID_GLASS_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
