package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Styx (Hades) -> "Volumetric Fire" (Hellfire)
private const val STYX_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float noise(float3 p) {
        float3 i = floor(p);
        float3 f = fract(p);
        f = f*f*(3.0-2.0*f);
        return mix(mix(mix(fract(sin(dot(i + float3(0,0,0), float3(12.9898,78.233,45.543))) * 43758.5453), 
                           fract(sin(dot(i + float3(1,0,0), float3(12.9898,78.233,45.543))) * 43758.5453), f.x),
                       mix(fract(sin(dot(i + float3(0,1,0), float3(12.9898,78.233,45.543))) * 43758.5453), 
                           fract(sin(dot(i + float3(1,1,0), float3(12.9898,78.233,45.543))) * 43758.5453), f.x), f.y),
                   mix(mix(fract(sin(dot(i + float3(0,0,1), float3(12.9898,78.233,45.543))) * 43758.5453), 
                           fract(sin(dot(i + float3(1,0,1), float3(12.9898,78.233,45.543))) * 43758.5453), f.x),
                       mix(fract(sin(dot(i + float3(0,1,1), float3(12.9898,78.233,45.543))) * 43758.5453), 
                           fract(sin(dot(i + float3(1,1,1), float3(12.9898,78.233,45.543))) * 43758.5453), f.x), f.y), f.z);
    }
    
    float fbm(float3 p) {
        float f = 0.0;
        f += 0.5000*noise(p); p *= 2.02;
        f += 0.2500*noise(p); p *= 2.03;
        f += 0.1250*noise(p);
        return f;
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        
        float3 ro = float3(0.0, 0.0, 2.0);
        float3 rd = normalize(float3(uv, -1.0));
        
        float3 p = ro;
        float t = 0.0;
        float4 sum = float4(0.0);
        
        for(int i=0; i<30; i++) {
            p = ro + rd * t;
            
            float3 q = p - float3(0.0, -1.0, 0.0); // Fire origin
            float len = length(q);
            
            // Noise animation
            float3 noisePos = q * 2.5 - float3(0.0, iTime * 2.0, 0.0);
            float n = fbm(noisePos);
            
            // Sphere shape with noise
            float d = len - 1.0 + n * 0.5;
            
            if (d < 0.1) {
                float alpha = (0.1 - d) * 2.0;
                // Fire gradient
                float3 col = mix(float3(1.0, 0.0, 0.0), float3(1.0, 1.0, 0.0), n);
                col = mix(col, float3(0.1), smoothstep(0.0, 1.0, p.y + 1.0)); // Smoke at top
                
                sum += float4(col, alpha) * (1.0 - sum.a);
                if (sum.a > 0.95) break;
            }
            t += 0.1;
        }
        
        return half4(sum.rgb, 1.0);
    }
"""

fun Modifier.hellfireEffect(isDark: Boolean): Modifier = runtimeShader(STYX_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}
