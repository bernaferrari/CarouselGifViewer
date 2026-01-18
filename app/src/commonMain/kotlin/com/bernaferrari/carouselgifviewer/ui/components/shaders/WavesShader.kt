package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 3. Waves -> "Bioluminescent Ocean" (Deep Sea with Glowing Plankton)
@Language("AGSL") private const val WAVES_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float hash(float2 p) { return fract(sin(dot(p, float2(127.1, 311.7))) * 43758.5453); }
    
    float noise(float2 p) {
        float2 i = floor(p);
        float2 f = fract(p);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(hash(i), hash(i + float2(1.0, 0.0)), f.x),
                   mix(hash(i + float2(0.0, 1.0)), hash(i + float2(1.0, 1.0)), f.x), f.y);
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.3;
        
        // Deep ocean gradient
        float3 col = mix(float3(0.0, 0.02, 0.08), float3(0.0, 0.05, 0.15), uv.y + 0.5);
        
        // Flowing caustic waves (inspired by Fireflies chromatic technique)
        for(float i = 1.0; i < 4.0; i++) {
            float2 p = uv * (2.0 + i * 0.5);
            p.x += sin(p.y * 3.0 + t * (0.5 + i * 0.2)) * 0.3;
            p.y += cos(p.x * 2.0 + t * 0.7) * 0.2;
            
            float wave = sin(p.x * 5.0 + t) * cos(p.y * 4.0 - t * 0.8);
            wave = 0.02 / abs(wave + 0.01);
            
            float3 caustic = float3(0.0, 0.4, 0.6) * wave * 0.15 / i;
            col += caustic;
        }
        
        // Bioluminescent particles (plankton glow)
        for(float i = 0.0; i < 25.0; i++) {
            float seed = i * 7.234;
            float2 pos = float2(
                sin(seed * 1.1 + t * 0.3) * 0.8,
                cos(seed * 0.7 + t * 0.2) * 0.6 + sin(t * 0.5 + seed) * 0.1
            );
            
            float d = length(uv - pos);
            float pulse = 0.5 + 0.5 * sin(t * 2.0 + seed * 5.0);
            float glow = 0.003 / (d * d + 0.005) * pulse;
            
            float3 planktonCol = mix(float3(0.0, 0.8, 0.9), float3(0.2, 0.6, 1.0), hash(float2(seed, 0.0)));
            col += planktonCol * glow * 0.15;
        }
        
        // Subtle depth rays
        float rays = sin(uv.x * 8.0 + uv.y * 4.0 - t) * 0.5 + 0.5;
        rays *= smoothstep(0.5, -0.2, uv.y);
        col += float3(0.0, 0.15, 0.25) * rays * 0.1;
        
        // Vignette for depth
        col *= 1.0 - length(uv) * 0.4;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.wavesEffect(isDark: Boolean): Modifier = runtimeShader(WAVES_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
