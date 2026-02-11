package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 1. Aurora
private const val AURORA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float3 col = float3(0.0);
        
        for(float i=1.0; i<4.0; i++) {
            float2 p = uv * i;
            p.y -= iTime * 0.1;
            float a = sin(p.x * 2.0 + iTime * 0.2) * 0.5 + 0.5;
            a += sin(p.y * 3.0 + iTime * 0.3) * 0.5 + 0.5;
            col += float3(0.1, 0.4 * i/4.0, 0.5) * a * (1.0/i);
        }
        
        return half4(col, 1.0);
    }
"""

fun Modifier.auroraEffect(isDark: Boolean): Modifier = runtimeShader(AURORA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
