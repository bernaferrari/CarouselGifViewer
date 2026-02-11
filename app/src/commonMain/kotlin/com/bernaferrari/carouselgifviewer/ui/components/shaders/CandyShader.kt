package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

private const val CANDY_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    
    float hash(float2 p) {
        float3 p3 = fract(float3(p.xyx) * 0.13);
        p3 += dot(p3, p3.yzx + 3.333);
        return fract((p3.x + p3.y) * p3.z);
    }

    float noise(float2 x) {
        float2 i = floor(x);
        float2 f = fract(x);
        f = f * f * (3.0 - 2.0 * f);
        float a = hash(i);
        float b = hash(i + float2(1.0, 0.0));
        float c = hash(i + float2(0.0, 1.0));
        float d = hash(i + float2(1.0, 1.0));
        return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
    }

    float fbm(float2 x) {
        float v = 0.0;
        float a = 0.5;
        float2x2 rot = float2x2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));
        for (int i = 0; i < 4; ++i) {
            v += a * noise(x);
            x = rot * x * 2.0 + 100.0;
            a *= 0.5;
        }
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        uv.x *= iResolution.x / iResolution.y;
        float t = iTime * 0.1 * PI;
        float2 t1 = float2(cos(t * 0.5), sin(t * 0.5));
        float2 t2 = float2(sin(t * 0.3), cos(t * 0.3));

        float2 q = float2(0.0);
        q.x = fbm(uv + t1 * 0.5);
        q.y = fbm(uv + float2(5.2, 1.3) + t2 * 0.5);

        float2 r = float2(0.0);
        r.x = fbm(uv + 4.0 * q + float2(1.7, 9.2) - t1 * 0.2);
        r.y = fbm(uv + 4.0 * q + float2(8.3, 2.8) + t2 * 0.2);

        float f = fbm(uv + 4.0 * r);

        half3 col = half3(0.0);
        half3 cPink = half3(1.0, 0.55, 0.75);
        half3 cCyan = half3(0.3, 0.85, 0.95);
        half3 cYellow = half3(1.0, 0.9, 0.5);
        half3 cDark = half3(0.5, 0.3, 0.6); 
        
        col = mix(cPink, cCyan, clamp(f * f * 4.0, 0.0, 1.0));
        col = mix(col, cYellow, clamp(length(q), 0.0, 1.0));
        col = mix(col, cDark, clamp(r.x, 0.0, 1.0));
        col *= 0.8 + 0.4 * f;
        
        float vignette = 1.0 - smoothstep(0.5, 1.5, length(uv - 0.5));
        col *= vignette;

        return half4(col, 1.0);
    }
"""

fun Modifier.candyEffect(isDark: Boolean): Modifier = runtimeShader(CANDY_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
