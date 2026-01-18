package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 4. Nebula -> "Celestial Bloom" (Living Chromatic Gas Clouds)
@Language("AGSL") private const val NEBULA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float hash(float2 p) { return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453); }
    
    float2 rotate(float2 p, float a) {
        float c = cos(a), s = sin(a);
        return float2(p.x * c - p.y * s, p.x * s + p.y * c);
    }
    
    float noise(float2 p) {
        float2 i = floor(p);
        float2 f = fract(p);
        f = f * f * f * (f * (f * 6.0 - 15.0) + 10.0); // Quintic interpolation
        return mix(mix(hash(i), hash(i + float2(1.0, 0.0)), f.x),
                   mix(hash(i + float2(0.0, 1.0)), hash(i + float2(1.0, 1.0)), f.x), f.y);
    }
    
    float fbm(float2 p, float t) {
        float v = 0.0;
        float a = 0.5;
        float2 shift = float2(100.0, 0.0);
        for (int i = 0; i < 6; i++) {
            v += a * noise(p);
            p = rotate(p, 0.5) * 2.0 + shift + t * 0.1;
            a *= 0.5;
        }
        return v;
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.15;
        
        // Domain warping for organic cloud movement (like DNA shader's depth)
        float2 q = float2(fbm(uv * 3.0 + t, t), fbm(uv * 3.0 + float2(5.2, 1.3) + t, t));
        float2 r = float2(fbm(uv * 3.0 + 4.0 * q + float2(1.7, 9.2), t), 
                         fbm(uv * 3.0 + 4.0 * q + float2(8.3, 2.8) + t * 0.5, t));
        float f = fbm(uv * 3.0 + 4.0 * r, t);
        
        // Chromatic separation (inspired by Fireflies)
        float3 col = float3(0.0);
        
        // Red channel - offset
        float fR = fbm(uv * 3.0 + 4.0 * r + float2(0.02, 0.0), t);
        // Blue channel - offset other direction  
        float fB = fbm(uv * 3.0 + 4.0 * r - float2(0.02, 0.0), t);
        
        // Deep space base
        col = mix(float3(0.05, 0.0, 0.1), float3(0.0, 0.0, 0.05), f);
        
        // Nebula clouds with chromatic effect
        col.r += smoothstep(0.3, 0.8, fR) * 0.6;
        col.g += smoothstep(0.4, 0.9, f) * 0.3;
        col.b += smoothstep(0.2, 0.7, fB) * 0.8;
        
        // Electric accents
        float electric = smoothstep(0.6, 0.61, f) * 0.8;
        col += float3(0.8, 0.4, 1.0) * electric;
        
        // Pulsing core glow
        float core = length(q) * 0.5;
        col += float3(1.0, 0.3, 0.6) * (0.1 / (core + 0.3)) * 0.15;
        
        // Vignette
        col *= 1.0 - length(uv) * 0.3;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.nebulaEffect(isDark: Boolean): Modifier = runtimeShader(NEBULA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
