package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

@Language("AGSL") private const val LAVALAMP_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;

    float blob(float2 uv, float2 pos, float size) {
        return size / length(uv - pos);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        uv.x *= iResolution.x / iResolution.y;
        half3 bg = half3(0.1, 0.05, 0.2);
        float t = iTime * 0.1 * PI;
        float potential = 0.0;
        
        float2 pos1 = float2(0.5 + 0.2 * sin(t), 0.5 + 0.3 * cos(t));
        potential += blob(uv, pos1, 0.08);
        float2 pos2 = float2(0.8 + 0.2 * cos(t * 2.0), 0.3 + 0.3 * sin(t * 2.0));
        potential += blob(uv, pos2, 0.1);
        float2 pos3 = float2(0.3 + 0.3 * sin(t * 3.0 + 1.0), 0.8 + 0.2 * cos(t * 2.0 + 2.0));
        potential += blob(uv, pos3, 0.09);
        float2 pos4 = float2(0.6 + 0.1 * cos(t * 3.0 + 4.0), 0.5 + 0.4 * sin(t));
        potential += blob(uv, pos4, 0.06);

        float edge = smoothstep(0.9, 1.0, potential);
        half3 blobColor1 = half3(1.0, 0.2, 0.5); 
        half3 blobColor2 = half3(1.0, 0.6, 0.0); 
        half3 goo = mix(blobColor1, blobColor2, uv.y);
        half3 color = mix(bg, goo, edge);
        color += goo * smoothstep(0.5, 0.9, potential) * 0.3;

        return half4(color, 1.0);
    }
"""

fun Modifier.lavaLampCreativeEffect(isDark: Boolean): Modifier = runtimeShader(LAVALAMP_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
