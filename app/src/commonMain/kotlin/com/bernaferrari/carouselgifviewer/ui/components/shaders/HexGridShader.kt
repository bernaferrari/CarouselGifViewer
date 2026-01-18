package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Plumber (Mario) -> "Hyper-Hexagons" (Pulsing Grid)
@Language("AGSL") private const val PLUMBER_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float hexDist(float2 p) {
        p = abs(p);
        float c = dot(p, normalize(float2(1.0, 1.73)));
        return max(c, p.x);
    }
    
    float4 hexCoords(float2 uv) {
        float2 r = float2(1.0, 1.73);
        float2 h = r * 0.5;
        float2 a = mod(uv, r) - h;
        float2 b = mod(uv - h, r) - h;
        float2 gv = dot(a, a) < dot(b, b) ? a : b;
        float2 id = uv - gv;
        return float4(gv, id);
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.5;
        
        // Tunnel transform
        float len = length(uv);
        float zoom = 0.5 / len + t;
        float angle = atan(uv.y, uv.x) / 6.28 + t * 0.1;
        float2 p = float2(angle * 4.0, zoom);
        
        float4 h = hexCoords(p * 5.0);
        float d = hexDist(h.xy);
        
        // Pulsing glow
        float glow = sin(length(h.zw) * 2.0 - t * 10.0) * 0.5 + 0.5;
        
        // Hex outline
        float line = smoothstep(0.45, 0.5, d);
        float fill = 1.0 - line;
        
        float3 col = float3(0.0);
        col += float3(1.0, 0.2, 0.5) * line * glow * 2.0; // Neon pink lines
        col += float3(0.1, 0.0, 0.2) * fill * glow; // Dark fill
        
        // Center fade
        col *= len * 2.0;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.hexGridEffect(isDark: Boolean): Modifier = runtimeShader(PLUMBER_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
