package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

private const val SILK_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    const float TAU = 6.28318530718;
    
    float hash(float2 p) {
        float3 p3 = fract(float3(p.xyx) * 0.1031);
        p3 += dot(p3, p3.yzx + 33.33);
        return fract((p3.x + p3.y) * p3.z);
    }
    
    float noise(float2 p) {
        float2 i = floor(p);
        float2 f = fract(p);
        f = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
        return mix(mix(hash(i), hash(i + float2(1.0, 0.0)), f.x),
                   mix(hash(i + float2(0.0, 1.0)), hash(i + float2(1.0, 1.0)), f.x), f.y);
    }
    
    float fbm(float2 p, float time) {
        float v = 0.0;
        float a = 0.5;
        float2x2 rot = float2x2(0.87, 0.5, -0.5, 0.87);
        for (int i = 0; i < 6; i++) {
            float2 drift = float2(sin(time + float(i) * 0.7), cos(time * 0.8 + float(i) * 0.5)) * 0.3;
            v += a * noise(p + drift);
            p = rot * p * 2.0 + 50.0;
            a *= 0.5;
        }
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
        
        float t = iTime * 0.05 * TAU; 
        float loopT1 = sin(t);
        float loopT2 = cos(t);
        float loopT3 = sin(t * 0.7 + 1.0);
        float loopT4 = cos(t * 0.5 + 2.0);
        
        float2 drift = float2(loopT1 * 0.15, loopT2 * 0.1);
        uv += drift;
        
        float2 q = float2(0.0);
        q.x = fbm(uv * 1.5 + float2(loopT1 * 0.3, loopT2 * 0.2), t * 0.3);
        q.y = fbm(uv * 1.5 + float2(5.2, 1.3) + float2(loopT3 * 0.25, loopT4 * 0.2), t * 0.25);
        
        float2 r = float2(0.0);
        r.x = fbm(uv * 1.8 + q * 3.0 + float2(1.7, 9.2) + float2(loopT2 * 0.2, loopT1 * 0.15), t * 0.35);
        r.y = fbm(uv * 1.8 + q * 3.0 + float2(8.3, 2.8) + float2(loopT4 * 0.2, loopT3 * 0.15), t * 0.28);
        
        float f = fbm(uv * 2.0 + r * 2.5, t * 0.4);
        float f2 = fbm(uv * 3.0 + q * 2.0 + float2(loopT1 * 0.1), t * 0.5);
        
        half3 cDeep = half3(0.02, 0.015, 0.04);
        half3 cWarm1 = half3(0.6, 0.15, 0.1);    
        half3 cWarm2 = half3(0.85, 0.4, 0.15);   
        half3 cGold = half3(0.95, 0.75, 0.35);   
        half3 cCool1 = half3(0.1, 0.2, 0.45);    
        half3 cCool2 = half3(0.25, 0.4, 0.55);   
        half3 cFlesh = half3(0.9, 0.7, 0.55);    
        half3 cCloud = half3(0.85, 0.82, 0.75);  
        
        half3 col = cDeep;
        float cloudBase = smoothstep(0.2, 0.7, f);
        float cloudDetail = smoothstep(0.3, 0.8, f2);
        
        col = mix(col, cCool1, smoothstep(0.1, 0.5, q.x) * 0.6);
        col = mix(col, cCool2, smoothstep(0.3, 0.7, q.y) * 0.4);
        
        
        float lightBeam = smoothstep(0.4, 0.9, r.x) * smoothstep(-0.3, 0.3, uv.y + loopT1 * 0.2);
        col = mix(col, cWarm2, lightBeam * 0.5);
        col = mix(col, cGold, pow(lightBeam, 2.0) * 0.4);
        
        col = mix(col, cCloud, cloudBase * 0.6);
        col = mix(col, cFlesh, cloudDetail * cloudBase * 0.3);
        
        float shadow = (1.0 - cloudBase) * smoothstep(0.3, 0.6, f2);
        col = mix(col, cWarm1, shadow * 0.25);
        
        float highlight = pow(f, 3.5) * cloudBase;
        col += cGold * highlight * 0.5;
        
        float glow = pow(smoothstep(0.6, 1.0, f * f2), 2.0);
        col += cCloud * glow * 0.3;
        
        col.r += (r.x - 0.5) * 0.08;
        col.b += (r.y - 0.5) * 0.06;
        
        float edge = length(uv);
        col *= 1.0 - edge * 0.2;
        col = pow(col, half3(0.95));
        col.r *= 1.02;
        col.b *= 0.98;

        return half4(col, 1.0);
    }
"""

fun Modifier.silkCreativeEffect(isDark: Boolean): Modifier = runtimeShader(SILK_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
