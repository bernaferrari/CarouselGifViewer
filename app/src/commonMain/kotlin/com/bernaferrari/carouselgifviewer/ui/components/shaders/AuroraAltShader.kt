package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

@Language("AGSL") private const val AURORA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    
    float gyroid(float3 p) {
        return dot(sin(p), cos(p.yzx));
    }

    float fbm(float3 p) {
        float v = 0.0;
        float a = 0.5;
        float s = 0.6;
        float c = 0.8;
        float2x2 rot = float2x2(c, -s, s, c);
        
        for (int i = 0; i < 4; i++) {
            float g = abs(gyroid(p));
            g = 1.0 - g; 
            v += g * a;
            p.xy = rot * p.xy;
            p = p * 2.0 + float3(g); 
            a *= 0.5;
        }
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
        float t = iTime * 0.1 * PI;
        float3 p = float3(uv * 1.5, sin(t * 0.5) * 1.5);
        float s = sin(t * 0.1);
        float c = cos(t * 0.1);
        p.xy = float2(p.x * c - p.y * s, p.x * s + p.y * c);
        float n = fbm(p);
        n = pow(max(0.0, n), 2.5);
        
        half3 col = half3(0.0);
        half3 cGreen = half3(0.0, 1.0, 0.5);
        half3 cPurple = half3(0.7, 0.0, 1.0);
        half3 cBlue = half3(0.0, 0.4, 1.0);
        
        float cMix = sin(n * 5.0 + t) * 0.5 + 0.5;
        col = mix(cGreen, cPurple, cMix);
        col = mix(col, cBlue, sin(length(uv) * 3.0 - t) * 0.5 + 0.5);
        col *= n * 2.5; 
        col += half3(0.01, 0.0, 0.03); 

        return half4(col, 1.0);
    }
"""

fun Modifier.auroraCreativeEffect(isDark: Boolean): Modifier = runtimeShader(AURORA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
