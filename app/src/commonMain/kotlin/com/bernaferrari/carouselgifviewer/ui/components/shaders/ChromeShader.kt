package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Host (Westworld) -> "Liquid Chrome" (Environment Mapping)
private const val HOST_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float2 p = -1.0 + 2.0 * uv;
        p.x *= iResolution.x / iResolution.y;
        
        float t = iTime * 0.3;
        
        // Warped Surface Normal
        float3 n = normalize(float3(
            sin(p.x*3.0 + t) + sin(p.y*4.0 + t*1.3) + sin(p.x*7.0 + p.y*3.0),
            cos(p.x*4.0 - t*0.4) + cos(p.y*3.0 + t) + cos(p.x*8.0 - p.y*4.0),
            1.0
        ));
        
        // Environment Colors (Studio Lighting)
        float3 ref = reflect(float3(0,0,-1), n);
        
        // Gradients simulating a horizon and lights
        float3 col = float3(0.0);
        col += float3(0.9, 0.9, 0.95) * pow(max(0.0, ref.y), 4.0); // Top soft light
        col += float3(0.1, 0.1, 0.15) * pow(max(0.0, -ref.y), 2.0); // Bottom dark
        
        // Sharp reflections
        col += float3(1.0) * smoothstep(0.95, 1.0, sin(ref.x * 10.0 + ref.y * 20.0));
        
        return half4(col, 1.0);
    }
"""

fun Modifier.chromeEffect(isDark: Boolean): Modifier = runtimeShader(HOST_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
