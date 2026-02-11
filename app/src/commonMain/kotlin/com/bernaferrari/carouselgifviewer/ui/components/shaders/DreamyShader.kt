package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Kirby Warp Star
// Kirby (Dream Land Clouds)
private const val KIRBY_WARP_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    // Simple noise for clouds
    float noise(float2 p) {
        return sin(p.x) * sin(p.y);
    }

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.2;
        
        // Pastel Gradient Background (Pink/Blue/Yellow)
        half3 col = mix(half3(1.0, 0.8, 0.9), half3(0.6, 0.8, 1.0), uv.y + 0.5);
        
        // Fluffy Clouds
        for(float i=1.0; i<4.0; i++) {
            float z = i;
            float2 p = uv * z + float2(t*0.5/z, 0.0);
            float cloud = noise(p * 3.0) * noise(p * 2.1 + 10.0);
            
            // Soft cloud shape
            float fullness = smoothstep(0.2, 0.8, cloud + 0.5);
            
            // Layer clouds onto color
            half3 cloudColor = half3(1.0, 0.95, 0.9); // Creamy white
            col = mix(col, cloudColor, fullness * 0.4 / z);
        }
        
        // Sparkles
        float sparkle = sin(uv.x * 20.0 + t*5.0) * sin(uv.y * 20.0 - t*3.0);
        if(sparkle > 0.95) col += half3(1.0, 0.9, 0.5) * (sparkle - 0.95) * 10.0;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.dreamyEffect(isDark: Boolean): Modifier = runtimeShader(KIRBY_WARP_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
