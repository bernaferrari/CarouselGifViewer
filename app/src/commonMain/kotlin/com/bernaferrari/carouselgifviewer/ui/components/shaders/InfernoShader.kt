package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// House of the Dragon (Dracarys Chaos)
private const val DRAGON_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    float hash(float2 p) {
        return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
    }

    float noise(float2 p) {
        float2 i = floor(p);
        float2 f = fract(p);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(hash(i), hash(i + float2(1.0, 0.0)), f.x),
                   mix(hash(i + float2(0.0, 1.0)), hash(i + float2(1.0, 1.0)), f.x), f.y);
    }

    float fbm(float2 p) {
        float v = 0.0;
        float a = 0.5;
        for (int i = 0; i < 3; i++) {
            v += a * noise(p);
            p *= 2.1;
            a *= 0.5;
        }
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float2 p = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.4;
        
        // Domain warping for organic flow
        float2 q = float2(fbm(p + t * 0.2), fbm(p + float2(1.0, 1.0)));
        float2 r = float2(fbm(p + 2.0 * q + float2(1.7, 9.2) + t * 0.15), 
                         fbm(p + 2.0 * q + float2(8.3, 2.8) + t * 0.12));
        float f = fbm(p + r);

        // Core fire intensity
        float intensity = f * f * (3.0 - 2.0 * f);
        intensity += 0.3 * sin(p.x * 2.0 + t) * cos(p.y * 2.0 - t);
        
        half3 col = half3(0.0);
        
        // Dark, mysterious palette: Deep Red -> Ember Orange -> Flickering Yellow
        half3 c1 = half3(0.02, 0.0, 0.01); // Dark base
        half3 c2 = half3(0.8, 0.2, 0.05); // Ember
        half3 c3 = half3(1.0, 0.7, 0.2);  // Glow
        
        col = mix(c1, c2, smoothstep(0.1, 0.6, intensity));
        col = mix(col, c3, smoothstep(0.5, 0.9, intensity));
        
        // Subtle smoke/void wisps
        float smoke = fbm(p * 1.5 - t * 0.2);
        col = mix(col, half3(0.05, 0.02, 0.08), smoke * 0.4 * (1.0 - intensity));
        
        // Vignette
        float vign = 1.0 - length(p) * 0.7;
        col *= clamp(vign, 0.0, 1.0);

        return half4(col, 1.0);
    }
"""

fun Modifier.infernoEffect(isDark: Boolean): Modifier = runtimeShader(DRAGON_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
