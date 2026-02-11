package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

private const val JUGGLING_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;

    float2 rotate(float2 p, float a) {
        float s = sin(a);
        float c = cos(a);
        return float2(p.x * c - p.y * s, p.x * s + p.y * c);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float aspect = iResolution.x / iResolution.y;
        uv.x *= aspect;
        
        float2 center = float2(0.5 * aspect, 0.5);
        float2 p = uv - center;
        float t = iTime * 0.15 * PI;
        
        float bgDist = length(p);
        half3 bgColor = half3(0.03, 0.03, 0.08);
        bgColor += half3(0.02, 0.01, 0.04) * (1.0 - smoothstep(0.0, 0.8, bgDist));
        
        half3 col = bgColor;
        
        for (int i = 0; i < 5; i++) {
            float phase = float(i) * (2.0 * PI / 5.0);
            float tt = t + phase;
            float x = 0.35 * sin(3.0 * tt);
            float y = 0.35 * sin(2.0 * tt);
            float2 pos = rotate(float2(x, y), t);
            float d = length(p - pos);
            half3 ballCol = half3(1.0);
            if(i==0) ballCol = half3(1.0, 0.2, 0.2); 
            if(i==1) ballCol = half3(1.0, 0.8, 0.2); 
            if(i==2) ballCol = half3(0.2, 1.0, 0.2); 
            if(i==3) ballCol = half3(0.2, 0.6, 1.0); 
            if(i==4) ballCol = half3(0.8, 0.2, 1.0); 
            float ambientGlow = 0.02 / (d * d + 0.02);
            col += ballCol * ambientGlow * 0.15;
        }
        
        for (int i = 0; i < 5; i++) {
            float phase = float(i) * (2.0 * PI / 5.0);
            half3 ballCol = half3(1.0);
            if(i==0) ballCol = half3(1.0, 0.2, 0.2); 
            if(i==1) ballCol = half3(1.0, 0.8, 0.2); 
            if(i==2) ballCol = half3(0.2, 1.0, 0.2); 
            if(i==3) ballCol = half3(0.2, 0.6, 1.0); 
            if(i==4) ballCol = half3(0.8, 0.2, 1.0); 
            
            for (int k = 0; k < 6; k++) {
                float trailLag = float(k) * 0.04;
                float tt = t + phase - trailLag;
                float x = 0.35 * sin(3.0 * tt);
                float y = 0.35 * sin(2.0 * tt);
                float2 pos = rotate(float2(x, y), t);
                float d = length(p - pos);
                float alpha = 1.0 - (float(k) / 6.0);
                alpha = pow(alpha, 1.5);
                float glow = 0.0008 / (d * d + 0.0005);
                float r = 0.06 * alpha;
                float core = smoothstep(r, r - 0.01, d);
                col += ballCol * (core * 0.9 + glow * 0.5) * alpha;
            }
        }
        return half4(col, 1.0);
    }
"""

fun Modifier.orbsEffect(isDark: Boolean): Modifier = runtimeShader(JUGGLING_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
