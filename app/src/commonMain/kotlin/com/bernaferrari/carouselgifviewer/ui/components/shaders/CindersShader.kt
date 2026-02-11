package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 15. Survivor -> "Ember Void" (Void-style volumetric ash and embers)
private const val SURVIVOR_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float2 rotate(float2 p, float a) {
        float c = cos(a), s = sin(a);
        return float2(c*p.x - s*p.y, s*p.x + c*p.y);
    }
    
    float hash(float3 p) {
        p = fract(p * 0.3183099 + 0.1);
        p *= 17.0;
        return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.2;
        
        // Apocalyptic orange-red base
        float3 col = float3(0.05, 0.02, 0.01);
        
        float3 ro = float3(0.0, 0.0, -3.0);
        float3 rd = normalize(float3(uv, 1.0));
        
        float ember = 0.0;
        float ash = 0.0;
        
        // Raymarch through ash storm
        for(int i = 0; i < 40; i++) {
            float3 p = ro + rd * float(i) * 0.12;
            
            // Slow rotation
            p.xy = rotate(p.xy, t * 0.1);
            p.xz = rotate(p.xz, t * 0.15);
            
            // Wind drift
            p.x += sin(p.z * 0.5 + t) * 0.5;
            p.y += t * 0.5; // Rising motion
            
            // Ash clouds (large soft shapes)
            float cloudNoise = sin(p.x * 2.0 + t) * sin(p.y * 1.5 - t * 0.5) * sin(p.z * 2.5 + t * 0.3);
            float cloud = length(p) - 1.5 + cloudNoise * 0.5;
            float ashDens = max(0.0, 0.15 - abs(cloud));
            ash += ashDens;
            
            // Ember particles (small bright points)
            float3 emberP = fract(p * 3.0) - 0.5;
            float emberDist = length(emberP) - 0.05;
            float emberDens = max(0.0, 0.1 - abs(emberDist));
            
            // Only some cells have embers
            float emberChance = hash(floor(p * 3.0));
            if(emberChance > 0.85) {
                // Pulsing ember
                float pulse = 0.5 + 0.5 * sin(t * 5.0 + emberChance * 20.0);
                ember += emberDens * pulse;
            }
            
            // Smoke wisps
            float wisps = sin(p.x * 8.0 + t * 2.0) * sin(p.y * 6.0 - t) * sin(p.z * 7.0 + t * 0.5);
            wisps = smoothstep(0.6, 1.0, abs(wisps));
            col += float3(0.15, 0.08, 0.05) * wisps * ashDens * 3.0;
        }
        
        // Ash color (gray-brown)
        col += float3(0.3, 0.25, 0.2) * ash * 0.8;
        
        // Ember color (bright orange-yellow)
        col += float3(1.0, 0.5, 0.1) * ember * 15.0;
        col += float3(1.0, 0.8, 0.3) * ember * 5.0; // Hot core
        
        // Atmospheric haze
        col += float3(0.1, 0.05, 0.02) * (1.0 - exp(-length(uv) * 2.0));
        
        // Subtle vignette
        col *= 1.0 - length(uv) * 0.3;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.cindersEffect(isDark: Boolean): Modifier = runtimeShader(SURVIVOR_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
