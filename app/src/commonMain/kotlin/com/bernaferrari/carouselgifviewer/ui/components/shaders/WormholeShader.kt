package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

@Language("AGSL") private const val VORTEX2_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265;
    
    mat2 rot(float a) {
        float c = cos(a), s = sin(a);
        return mat2(c, -s, s, c);
    }
    
    half3 palette(float t) {
        return 0.5 + 0.5 * cos(6.28318 * (float3(0.5) * t + float3(0.0, 0.33, 0.67)));
    }

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
        float2 uv0 = uv;
        
        uv *= 0.6; 
        uv0 *= 0.6;
        
        float t = iTime * 0.2;
        float3 finalColor = float3(0.0);
        float r = length(uv);
         
        for (float i = 0.0; i < 3.0; i++) {
            float angle = atan(uv.y, uv.x);
            float spiral = angle + 2.0 * log(length(uv) + 0.1) - t * (1.0 + i * 0.5);
            float2 p = float2(spiral, length(uv));
            float matter = sin(p.x * 5.0 + i * 2.0) * sin(p.y * 10.0 - t);
            matter = abs(matter);
            matter = pow(0.02 / matter, 1.2); 
            float ringFade = smoothstep(0.15, 0.4, p.y) * smoothstep(2.5, 0.8, p.y);
            half3 col = palette(length(uv0) + i * 0.4 + t);
            finalColor += col * matter * ringFade;
            uv = rot(0.2) * uv;
        }
        
        finalColor += float3(0.02, 0.01, 0.05);
        finalColor *= smoothstep(3.0, 1.0, length(uv));
        return half4(finalColor, 1.0);
    }
"""

fun Modifier.wormholeEffect(isDark: Boolean): Modifier = runtimeShader(VORTEX2_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
