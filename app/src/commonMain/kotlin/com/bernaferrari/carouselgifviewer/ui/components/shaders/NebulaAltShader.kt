package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

private const val NEBULA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
    }

    float noise(float2 st) {
        float2 i = floor(st);
        float2 f = fract(st);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(random(i + float2(0.0, 0.0)), random(i + float2(1.0, 0.0)), f.x),
                   mix(random(i + float2(0.0, 1.0)), random(i + float2(1.0, 1.0)), f.x), f.y);
    }

    float fbm(float2 st) {
        float value = 0.0;
        float amplitude = 0.5;
        for (int i = 0; i < 5; i++) {
            value += amplitude * noise(st);
            st *= 2.0;
            amplitude *= 0.5;
        }
        return value;
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        uv.x *= iResolution.x / iResolution.y; 
        float time = iTime * 0.15; 

        float2 q = float2(0.0);
        q.x = fbm(uv + 0.0 * time);
        q.y = fbm(uv + float2(1.0));

        float2 r = float2(0.0);
        r.x = fbm(uv + 1.0 * q + float2(1.7, 9.2) + 0.15 * time);
        r.y = fbm(uv + 1.0 * q + float2(8.3, 2.8) + 0.126 * time);

        float f = fbm(uv + r);

        half3 color = mix(half3(0.1, 0.0, 0.2), half3(0.3, 0.05, 0.3), clamp(f * f * 4.0, 0.0, 1.0));
        color = mix(color, half3(0.0, 0.3, 0.4), clamp(length(q), 0.0, 1.0));
        color = mix(color, half3(0.1, 0.0, 0.1), clamp(length(r.x), 0.0, 1.0));
        
        half3 highlight = half3(1.0, 0.8, 0.6) * pow(f, 3.0);
        color += highlight;

        float2 center = fragCoord / iResolution.xy - 0.5;
        float vign = 1.0 - dot(center, center) * 0.8;
        color *= vign;

        return half4(color, 1.0);
    }
"""

fun Modifier.nebulaCreativeEffect(isDark: Boolean): Modifier = runtimeShader(NEBULA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
