package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 2. LavaLamp
@Language("AGSL") private const val LAVA_LAMP_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float t = iTime * 0.5;
        
        float3 col = float3(0.0);
        float2 p = uv * 4.0 - 2.0;
        
        for(float i=1.0; i<6.0; i++) {
            p.x += 0.3/i * sin(i * 3.0 * p.y + t);
            p.y += 0.3/i * cos(i * 3.0 * p.x + t);
        }
        
        float r = cos(p.x + p.y + 1.0) * 0.5 + 0.5;
        float g = sin(p.x + p.y + 1.0) * 0.5 + 0.5;
        float b = (sin(p.x + p.y) + cos(p.x + p.y)) * 0.5 + 0.5;
        
        col = float3(r, g, b);
        col = mix(col, float3(1.0, 0.5, 0.0), 0.5); // Orange tint
        
        return half4(col, 1.0);
    }
"""

fun Modifier.lavaLampEffect(isDark: Boolean): Modifier = runtimeShader(LAVA_LAMP_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
