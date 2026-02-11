package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 17. XCom -> "Radar" (Tactical Sweep)
private const val TACTICAL_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float r = length(uv);
        float a = atan(uv.y, uv.x);
        
        float3 col = float3(0.0);
        
        // Rings
        float ring = abs(sin(r * 20.0));
        col += float3(0.0, 0.3, 0.0) * smoothstep(0.1, 0.0, ring) * 0.5;
        
        // Sweep
        float sweep = smoothstep(0.0, 1.0, 1.0 - abs(a - sin(iTime))); // Simple sweep? No, rotating.
        // Rotating sweep
        float angle = -iTime * 2.0;
        // Normalize angle difference
        float diff = mod(a - angle, 6.2831) - 3.1415; // wrap
        // Assume sweep is localized
        float beam = smoothstep(0.0, 0.5, mod(a - angle + 3.14, 6.28) - 3.14 + 1.0); 
        // Better:
        float sweepAng = mod(a + iTime * 3.0, 6.2831);
        col += float3(0.0, 1.0, 0.0) * smoothstep(5.0, 6.28, sweepAng) * step(r, 0.45);
        
        // Blips
        float blip = step(0.99, sin(uv.x * 50.0)*sin(uv.y*50.0 + iTime));
        if(r < 0.45 && sweepAng > 6.0) col += float3(1.0) * blip;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.radarEffect(isDark: Boolean): Modifier = runtimeShader(TACTICAL_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
