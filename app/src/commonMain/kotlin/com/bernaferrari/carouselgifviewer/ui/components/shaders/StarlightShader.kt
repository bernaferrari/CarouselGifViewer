package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import org.intellij.lang.annotations.Language
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 5. Kirby -> "Star Nest" (Galaxy)
@Language("AGSL") private const val KIRBY_CITY_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float3 dir = normalize(float3(uv, 1.0));
        float3 from = float3(1.0, 0.5, 0.5);
        from += float3(iTime * 2.0e-2, iTime * 2.0e-2, -2.0);
        
        // Scaling
        float s = 0.1, fade = 1.0;
        float darkmatter = 0.300;
        float3 v = float3(0.0);
        
        for (int r = 0; r < 12; r++) { // volumetric steps
            float3 p = from + s * dir * 0.5;
            p = abs(float3(0.8) - mod(p, float3(0.8 * 2.0))); // tiling fold
            
            float pa = 0.0, a = 0.0;
            for (int i = 0; i < 9; i++) { // iterations
                p = abs(p) / dot(p, p) - 0.53; // the magic formula
                a += abs(length(p) - pa);
                pa = length(p);
            }
            
            float dm = max(0.0, darkmatter - a * a * 0.001); // darken shift
            a *= a * a; // contrast
            
            if (r > 6) fade *= 1.0 - dm; // dark matter, don't render near
            
            v += fade;
            v += float3(s, s*s, s*s*s*s) * a * 0.001 * fade; // coloring based on distance
            fade *= 0.73; // distance fading
            s += 0.1;
        }
        
        v = mix(float3(length(v)), v, 0.8); // saturation
        return half4(v * 0.01, 1.0);
    }
"""

fun Modifier.starlightEffect(isDark: Boolean): Modifier = runtimeShader(KIRBY_CITY_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
