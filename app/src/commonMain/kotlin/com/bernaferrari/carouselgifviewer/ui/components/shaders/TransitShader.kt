package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 11. Subway -> "Tube Lights" (Dynamic Vertical Flow - Living Map)
private const val SUBWAY_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float3 col = float3(0.05);
        
        // Multiple vertical lines
        for(float i=0.0; i<7.0; i++) {
            float xPos = (fract(sin(i) * 564.3) - 0.5) * 1.5;
            float speed = 0.5 + fract(i*0.2);
            
            // Base wave
            float yWave = sin(uv.y * 3.0 + iTime * speed + i * 10.0) * 0.1;
            float d = abs(uv.x - xPos + yWave);
            float w = 0.005 + 0.01 * sin(iTime + i);
            float mask = smoothstep(w + 0.01, w, d);
            
            // Pulse
            float pulse = smoothstep(0.0, 1.0, sin(uv.y * 5.0 + iTime * 2.0 + i * 13.0));
            
            // Base Color
            float3 color = 0.5 + 0.5 * cos(3.14 + i * 0.8 + float3(0.0, 0.33, 0.67));
            
            // Overrides for specific distinct lines
            bool isSpecial = false;
            if (abs(i - 2.0) < 0.1) { color = float3(1.0); isSpecial = true; } // White
            if (abs(i - 5.0) < 0.1) { color = float3(0.8, 0.4, 0.1); isSpecial = true; } // Brown
            
            col += color * mask * (0.2 + 0.8 * pulse);
            
            // Extra "ghost" waves appearing from special lines
            if (isSpecial) {
                float t2 = iTime * 1.5;
                // A secondary wave that separates and merges
                float split = sin(uv.y * 10.0 - t2) * 0.05 * sin(t2); 
                float d2 = abs(uv.x - xPos + yWave + split);
                float mask2 = smoothstep(w*0.5 + 0.01, w*0.5, d2);
                
                // Visibility breathes
                float appear = smoothstep(0.2, 0.8, sin(t2 * 0.5 + uv.y * 2.0));
                
                col += color * mask2 * 0.6 * appear;
            }
        }
        
        // Vignette
        col *= smoothstep(0.8, 0.2, length(uv));
        
        return half4(col, 1.0);
    }
"""

fun Modifier.transitEffect(isDark: Boolean): Modifier = runtimeShader(SUBWAY_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
