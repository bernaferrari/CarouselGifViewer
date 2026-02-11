package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

private const val WAVES_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    uniform half4 color1;
    uniform half4 color2;
    uniform half4 color3;

    const float PI = 3.14159265359;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution;
        float t = iTime * 0.2;
        uv.y += 0.05 * sin(uv.x * 10.0 + t);
        uv.x += 0.05 * cos(uv.y * 10.0 + t * 0.8);
        
        float wave1 = sin(uv.x * 8.0 + t + uv.y * 3.0);
        float wave2 = sin(uv.x * 12.0 - t * 0.5 + uv.y * 6.0);
        float wave3 = sin(uv.x * 4.0 + t * 1.5 + uv.y * 9.0);
        
        float combined = (wave1 + wave2 + wave3) / 3.0; 
        float value = combined * 0.5 + 0.5;
        value = smoothstep(0.2, 0.8, value);

        float mixVal = uv.y + value * 0.2;
        mixVal = clamp(mixVal, 0.0, 1.0);
        half4 c = mix(color1, color2, mixVal);
        float highlight = smoothstep(0.85, 0.95, value);
        c = mix(c, color3, highlight * 0.5); 
        
        return half4(c.rgb, 1.0);
    }
"""

fun Modifier.wavesCreativeEffect(isDark: Boolean): Modifier = runtimeShader(WAVES_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
    setFloatUniform("color1", 0.1f, 0.2f, 0.6f, 1.0f) 
    setFloatUniform("color2", 0.0f, 0.7f, 0.8f, 1.0f)
    setFloatUniform("color3", 0.9f, 0.95f, 1.0f, 1.0f)
}
