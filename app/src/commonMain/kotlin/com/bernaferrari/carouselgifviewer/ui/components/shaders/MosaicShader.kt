package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

@Language("AGSL") private const val MOSAIC_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    
    float2 rotate(float2 v, float a) {
        float s = sin(a);
        float c = cos(a);
        return float2(v.x * c - v.y * s, v.x * s + v.y * c);
    }
    
    half3 palette(float t) {
        half3 a = half3(0.5, 0.5, 0.5);
        half3 b = half3(0.5, 0.5, 0.5);
        half3 c = half3(1.0, 1.0, 1.0);
        half3 d = half3(0.263, 0.416, 0.557);
        return a + b * cos(6.28318 * (c * t + d));
    }

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
        float2 uv0 = uv;
        float t = iTime * 0.05 * PI;
        half3 finalColor = half3(0.0);
        
        float zoom = 1.0 + 0.2 * sin(t * 0.1); 
        
        for (float i = 0.0; i < 4.0; i++) {
            uv = abs(uv * (1.5 * zoom)) - 0.5;
            float d = length(uv) * exp(-length(uv0));
            half3 col = palette(length(uv0) + i * 0.4 + t * 0.2);
            d = sin(d * 8.0 + t) / 8.0;
            d = abs(d);
            d = pow(0.01 / d, 1.2);
            finalColor += col * d;
            uv = rotate(uv, t * 0.05 + i * 0.5);
        }
        
        float centerFade = smoothstep(0.0, 0.5, length(uv0));
        finalColor *= 0.5 + 0.5 * centerFade; 
        
        return half4(finalColor, 1.0);
    }
"""

fun Modifier.mosaicEffect(isDark: Boolean): Modifier = runtimeShader(MOSAIC_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
