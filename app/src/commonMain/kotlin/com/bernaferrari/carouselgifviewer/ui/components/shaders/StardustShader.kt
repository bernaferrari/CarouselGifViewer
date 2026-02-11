package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// 18. Cosmic -> "Star Layers" (User Snippet 1)
private const val COSMIC_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    // Hash function to replace texture lookup
    float Hash21(float2 p) {
        p = fract(p * float2(123.34, 456.21));
        p += dot(p, p + 45.32);
        return fract(p.x * p.y);
    }
    
    // Rotation
    float2 Rot(float2 p, float a) {
        float c = cos(a), s = sin(a);
        return float2(p.x*c - p.y*s, p.x*s + p.y*c);
    }
    
    float Star(float2 uv, float flare) {
        float d = length(uv);
        float m = 0.02 / d;
        
        float rays = max(0.0, 1.0 - abs(uv.x * uv.y * 1000.0));
        m += rays * flare;
        
        uv = Rot(uv, 3.1415/4.0);
        rays = max(0.0, 1.0 - abs(uv.x * uv.y * 1000.0));
        m += rays * 0.3 * flare;
        
        m *= smoothstep(1.0, 0.2, d);
        return m;
    }
    
    float3 StarLayer(float2 uv) {
        float3 col = float3(0.0);
        
        float2 gv = fract(uv) - 0.5;
        float2 id = floor(uv);
        
        for(int y = -1; y <= 1; y++ ) {
            for(int x = -1; x <= 1; x++) {
                float2 offs = float2(float(x), float(y));

                float n = Hash21(id + offs);
                float size = fract(n * 345.32);
                
                float2 p = float2(n, fract(n * 34.0));
                
                float star = Star(gv - offs - p + 0.5, smoothstep(0.8, 1.0, size) * 0.6);
                
                // Color shift based on hash instead of texture
                // vec3 hueShift = fract(n*2345.2 + dot(uv /420.,texture(iChannel0, vec2(0.25, 0.)).rg))*vec3(.2, .3, .9)*123.2;
                // Simplified hue shift:
                float hueSeed = fract(n * 2345.2);
                float3 baseHue = float3(0.2, 0.3, 0.9);
                float3 hueShift = hueSeed * baseHue * 123.2;

                float3 color = sin(hueShift) * 0.5 + 0.5;
                color = color * float3(1.0, 0.25, 1.0 + size);

                star *= sin(iTime * 3.0 + n * 6.2831) * 0.4 + 1.0;
                col += star * size * color;
            }
        }
        return col;
    }

    float2 rotate(float2 p, float a) {
        float c = cos(a), s = sin(a);
        return float2(c*p.x - s*p.y, s*p.x + c*p.y);
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.1;
        
        float3 col = float3(0.0);
        
        float3 ro = float3(0.0, 0.0, -3.0);
        float3 rd = normalize(float3(uv, 1.0));
        
        // Raymarch filaments
        float dens = 0.0;
        for(int i=0; i<30; i++) {
            float3 p = ro + rd * float(i) * 0.2;
            
            p.xy = rotate(p.xy, t);
            
            // Filament noise
            float n = sin(p.x*5.0) * sin(p.y*5.0) * sin(p.z*5.0);
            n += sin(p.x*10.0 + t) * 0.2;
            
            // Web structure
            float web = 0.05 / abs(n);
            
            // Color
            float3 c = float3(0.5, 0.2, 1.0) * web * 0.1; // Violet web
            
            col += c * (1.0 - dens);
            dens += web * 0.05;
            if (dens > 1.0) break;
        }
        
        col *= 0.5; // Exposure
        
        // Deep space background
        col += float3(0.0, 0.0, 0.1) * (1.0 - dens);
        
        return half4(col, 1.0);
    }
"""

fun Modifier.stardustEffect(isDark: Boolean): Modifier = runtimeShader(COSMIC_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
