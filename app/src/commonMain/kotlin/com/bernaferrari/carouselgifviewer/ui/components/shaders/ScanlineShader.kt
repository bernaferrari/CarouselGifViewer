package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

private const val FALLOUT_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float rand(float2 n) { 
        return fract(sin(dot(n, float2(12.9898, float2(12.9898, 4.1414).y))) * 43758.5453);
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float2 p = uv * 2.0 - 1.0;
        p.x *= iResolution.x / iResolution.y;
        
        // Curvature
        p *= 1.0 + pow(length(p)*0.4, 2.0);
        
        if(abs(p.x) > 1.0 || abs(p.y) > 1.0) return half4(0.0, 0.0, 0.0, 1.0);
        
        float3 col = float3(0.0);
        
        // Scanlines
        float scanline = sin(uv.y * 200.0 + iTime * 10.0);
        col += float3(0.0, 0.1, 0.0) * (0.5 + 0.5 * scanline);
        
        // Text blocks (Abstract)
        float2 grid = floor(uv * float2(20.0, 40.0));
        float char = step(0.5, rand(grid + floor(iTime * 10.0)));
        
        // Only on edges
        float mask = smoothstep(0.3, 0.4, abs(uv.x - 0.5)) + smoothstep(0.3, 0.4, abs(uv.y - 0.5));
        
        col += float3(0.2, 1.0, 0.4) * char * mask * 0.8;
        
        // Bloom
        col += float3(0.0, 0.4, 0.1) * length(p);
        
        return half4(col, 1.0);
    }
"""

fun Modifier.scanlineEffect(isDark: Boolean): Modifier = runtimeShader(FALLOUT_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
