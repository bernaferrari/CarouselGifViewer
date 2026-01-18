package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Void (Hollow Knight) -> "Ether" (Swirling Nebula)
@Language("AGSL") private const val VOID_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float2 rotate(float2 p, float a) {
        float c = cos(a), s = sin(a);
        return float2(c*p.x - s*p.y, s*p.x + c*p.y);
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.2;
        
        float3 col = float3(0.0);
        float3 ro = float3(0.0, 0.0, -2.0);
        float3 rd = normalize(float3(uv, 1.0));
        
        for(int i=0; i<30; i++) {
            float3 p = ro + rd * float(i) * 0.1;
            p.xy = rotate(p.xy, t * 0.5);
            p.xz = rotate(p.xz, t * 0.2);
            
            // Nebula noise
            float n = sin(p.x*3.0+t) + sin(p.y*4.0-t) + sin(p.z*5.0);
            float d = length(p) - 1.0 + n * 0.2;
            
            float dens = max(0.0, 0.1 - abs(d));
            col += float3(0.1, 0.05, 0.2) * dens * 10.0; // Purple base
            col += float3(0.2, 0.4, 0.8) * dens * 5.0 * sin(t + p.x); // Blue highlights
        }
        
        return half4(col, 1.0);
    }
"""

fun Modifier.etherEffect(isDark: Boolean): Modifier = runtimeShader(VOID_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
