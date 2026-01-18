package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Elden Ring (Golden Order)
@Language("AGSL") private const val ELDEN_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.5;
        
        // Background - Dark fantasy
        half3 col = half3(0.05, 0.05, 0.0); 
        
        // Golden Arcs (Erdtree Rings)
        for(float i=0.0; i<5.0; i++) {
             float r = length(uv);
             float angle = atan(uv.y, uv.x);
             
             float arc = abs(r - (0.3 + i*0.1) + sin(angle*0.5 + t)*0.05) - 0.005;
             float glow = 0.002 / max(arc, 0.001);
             
             // Break gaps in rings
             float gap = sin(angle * (5.0 + i) + t);
             if (gap > 0.5) glow = 0.0;
             
             col += half3(1.0, 0.8, 0.2) * glow * 0.5;
        }
        
        // Particles (Leaves)
        // ... (simplified)
        
        // Fog
        col += half3(0.2, 0.15, 0.05) * (uv.y + 0.5);

        return half4(col, 1.0);
    }
"""

fun Modifier.goldenRingsEffect(isDark: Boolean): Modifier = runtimeShader(ELDEN_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
