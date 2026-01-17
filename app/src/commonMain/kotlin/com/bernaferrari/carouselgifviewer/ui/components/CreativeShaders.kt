package com.bernaferrari.carouselgifviewer.ui.components

import androidx.compose.ui.Modifier

// ==============================================================================
// 1. ORIGINAL SHADERS (First Generation)
// ==============================================================================

private const val AURORA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    
    float gyroid(float3 p) {
        return dot(sin(p), cos(p.yzx));
    }

    float fbm(float3 p) {
        float v = 0.0;
        float a = 0.5;
        float s = 0.6;
        float c = 0.8;
        float2x2 rot = float2x2(c, -s, s, c);
        
        for (int i = 0; i < 4; i++) {
            float g = abs(gyroid(p));
            g = 1.0 - g; 
            v += g * a;
            p.xy = rot * p.xy;
            p = p * 2.0 + float3(g); 
            a *= 0.5;
        }
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
        float t = iTime * 0.1 * PI;
        float3 p = float3(uv * 1.5, sin(t * 0.5) * 1.5);
        float s = sin(t * 0.1);
        float c = cos(t * 0.1);
        p.xy = float2(p.x * c - p.y * s, p.x * s + p.y * c);
        float n = fbm(p);
        n = pow(max(0.0, n), 2.5);
        
        half3 col = half3(0.0);
        half3 cGreen = half3(0.0, 1.0, 0.5);
        half3 cPurple = half3(0.7, 0.0, 1.0);
        half3 cBlue = half3(0.0, 0.4, 1.0);
        
        float cMix = sin(n * 5.0 + t) * 0.5 + 0.5;
        col = mix(cGreen, cPurple, cMix);
        col = mix(col, cBlue, sin(length(uv) * 3.0 - t) * 0.5 + 0.5);
        col *= n * 2.5; 
        col += half3(0.01, 0.0, 0.03); 

        return half4(col, 1.0);
    }
"""

fun Modifier.auroraEffect(isDark: Boolean): Modifier = runtimeShader(AURORA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isAuroraShaderSupported: Boolean = true

// Lava Lamp Shader
private const val LAVALAMP_SHADER_AGSL = """
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

fun Modifier.lavaLampEffect(isDark: Boolean): Modifier = runtimeShader(LAVALAMP_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isLavaLampShaderSupported: Boolean = true

// Waves Shader
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

fun Modifier.wavesEffect(isDark: Boolean): Modifier = runtimeShader(WAVES_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
    setFloatUniform("color1", 0.1f, 0.2f, 0.6f, 1.0f) 
    setFloatUniform("color2", 0.0f, 0.7f, 0.8f, 1.0f)
    setFloatUniform("color3", 0.9f, 0.95f, 1.0f, 1.0f)
}

val isWavesShaderSupported: Boolean = true

// Nebula Shader
private const val NEBULA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
    }

    float noise(float2 st) {
        float2 i = floor(st);
        float2 f = fract(st);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(random(i + float2(0.0, 0.0)), random(i + float2(1.0, 0.0)), f.x),
                   mix(random(i + float2(0.0, 1.0)), random(i + float2(1.0, 1.0)), f.x), f.y);
    }

    float fbm(float2 st) {
        float value = 0.0;
        float amplitude = 0.5;
        for (int i = 0; i < 5; i++) {
            value += amplitude * noise(st);
            st *= 2.0;
            amplitude *= 0.5;
        }
        return value;
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        uv.x *= iResolution.x / iResolution.y; 
        float time = iTime * 0.15; 

        float2 q = float2(0.0);
        q.x = fbm(uv + 0.0 * time);
        q.y = fbm(uv + float2(1.0));

        float2 r = float2(0.0);
        r.x = fbm(uv + 1.0 * q + float2(1.7, 9.2) + 0.15 * time);
        r.y = fbm(uv + 1.0 * q + float2(8.3, 2.8) + 0.126 * time);

        float f = fbm(uv + r);

        half3 color = mix(half3(0.1, 0.0, 0.2), half3(0.3, 0.05, 0.3), clamp(f * f * 4.0, 0.0, 1.0));
        color = mix(color, half3(0.0, 0.3, 0.4), clamp(length(q), 0.0, 1.0));
        color = mix(color, half3(0.1, 0.0, 0.1), clamp(length(r.x), 0.0, 1.0));
        
        half3 highlight = half3(1.0, 0.8, 0.6) * pow(f, 3.0);
        color += highlight;

        float2 center = fragCoord / iResolution.xy - 0.5;
        float vign = 1.0 - dot(center, center) * 0.8;
        color *= vign;

        return half4(color, 1.0);
    }
"""

fun Modifier.nebulaEffect(isDark: Boolean): Modifier = runtimeShader(NEBULA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isNebulaShaderSupported: Boolean = true

// Candy Shader
private const val CANDY_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    
    float hash(float2 p) {
        float3 p3 = fract(float3(p.xyx) * 0.13);
        p3 += dot(p3, p3.yzx + 3.333);
        return fract((p3.x + p3.y) * p3.z);
    }

    float noise(float2 x) {
        float2 i = floor(x);
        float2 f = fract(x);
        f = f * f * (3.0 - 2.0 * f);
        float a = hash(i);
        float b = hash(i + float2(1.0, 0.0));
        float c = hash(i + float2(0.0, 1.0));
        float d = hash(i + float2(1.0, 1.0));
        return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
    }

    float fbm(float2 x) {
        float v = 0.0;
        float a = 0.5;
        float2x2 rot = float2x2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));
        for (int i = 0; i < 4; ++i) {
            v += a * noise(x);
            x = rot * x * 2.0 + 100.0;
            a *= 0.5;
        }
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        uv.x *= iResolution.x / iResolution.y;
        float t = iTime * 0.1 * PI;
        float2 t1 = float2(cos(t * 0.5), sin(t * 0.5));
        float2 t2 = float2(sin(t * 0.3), cos(t * 0.3));

        float2 q = float2(0.0);
        q.x = fbm(uv + t1 * 0.5);
        q.y = fbm(uv + float2(5.2, 1.3) + t2 * 0.5);

        float2 r = float2(0.0);
        r.x = fbm(uv + 4.0 * q + float2(1.7, 9.2) - t1 * 0.2);
        r.y = fbm(uv + 4.0 * q + float2(8.3, 2.8) + t2 * 0.2);

        float f = fbm(uv + 4.0 * r);

        half3 col = half3(0.0);
        half3 cPink = half3(1.0, 0.55, 0.75);
        half3 cCyan = half3(0.3, 0.85, 0.95);
        half3 cYellow = half3(1.0, 0.9, 0.5);
        half3 cDark = half3(0.5, 0.3, 0.6); 
        
        col = mix(cPink, cCyan, clamp(f * f * 4.0, 0.0, 1.0));
        col = mix(col, cYellow, clamp(length(q), 0.0, 1.0));
        col = mix(col, cDark, clamp(r.x, 0.0, 1.0));
        col *= 0.8 + 0.4 * f;
        
        float vignette = 1.0 - smoothstep(0.5, 1.5, length(uv - 0.5));
        col *= vignette;

        return half4(col, 1.0);
    }
"""

fun Modifier.candyEffect(isDark: Boolean): Modifier = runtimeShader(CANDY_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isCandyShaderSupported: Boolean = true

// Juggling Shader
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

fun Modifier.jugglingEffect(isDark: Boolean): Modifier = runtimeShader(JUGGLING_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isJugglingShaderSupported: Boolean = true

// Vortex Shader
private const val VORTEX_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    uniform half4 color1;
    uniform half4 color2;
    uniform half4 color3;

    const float SPIN_ROTATION = -2.0;
    const float SPIN_SPEED = 2.0;
    const float2 OFFSET = float2(0.0);
    const float CONTRAST = 2.5;
    const float LIGHTING = 0.4;
    const float SPIN_AMOUNT = 0.4;
    const float PIXEL_FILTER = 1024.0;
    const float SPIN_EASE = 1.0;
    const bool IS_ROTATE = false;

    half4 main(float2 fragCoord) {
        float2 screenSize = iResolution;
        float pixel_size = length(screenSize.xy) / PIXEL_FILTER;
        float2 uv = (floor(fragCoord.xy*(1.0/pixel_size))*pixel_size - 0.5*screenSize.xy)/length(screenSize.xy) - OFFSET;
        float uv_len = length(uv);
        
        float speed = (SPIN_ROTATION*SPIN_EASE*0.2);
        if(IS_ROTATE){
           speed = iTime * speed;
        }
        speed += 302.2;
        float new_pixel_angle = atan(uv.y, uv.x) + speed - SPIN_EASE*20.0*(1.0*SPIN_AMOUNT*uv_len + (1.0 - 1.0*SPIN_AMOUNT));
        float2 mid = (screenSize.xy/length(screenSize.xy))/2.0;
        uv = (float2((uv_len * cos(new_pixel_angle) + mid.x), (uv_len * sin(new_pixel_angle) + mid.y)) - mid);
        
        uv *= 30.0;
        speed = iTime*(SPIN_SPEED);
        float2 uv2 = float2(uv.x+uv.y);
        
        for(int i=0; i < 5; i++) {
            uv2 += sin(max(uv.x, uv.y)) + uv;
            uv  += 0.5*float2(cos(5.1123314 + 0.353*uv2.y + speed*0.131121),sin(uv2.x - 0.113*speed));
            uv  -= 1.0*cos(uv.x + uv.y) - 1.0*sin(uv.x*0.711 - uv.y);
        }
        
        float contrast_mod = (0.25*CONTRAST + 0.5*SPIN_AMOUNT + 1.2);
        float paint_res = min(2.0, max(0.0,length(uv)*(0.035)*contrast_mod));
        float c1p = max(0.0,1.0 - contrast_mod*abs(1.0-paint_res));
        float c2p = max(0.0,1.0 - contrast_mod*abs(paint_res));
        float c3p = 1.0 - min(1.0, c1p + c2p);
        float light = (LIGHTING - 0.2)*max(c1p*5.0 - 4.0, 0.0) + LIGHTING*max(c2p*5.0 - 4.0, 0.0);
        
        return (0.3/CONTRAST)*color1 + (1.0 - 0.3/CONTRAST)*(color1*c1p + color2*c2p + half4(c3p*color3.rgb, c3p*color1.a)) + light;
    }
"""

fun Modifier.vortexEffect(isDark: Boolean): Modifier = runtimeShader(VORTEX_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
    setFloatUniform("color1", 0.29f, 0.05f, 0.45f, 1.0f) // Deep Violet
    setFloatUniform("color2", 0.0f, 0.85f, 0.8f, 1.0f)   // Vivid Teal
    setFloatUniform("color3", 0.9f, 0.1f, 0.5f, 1.0f)    // Hot Pink
}

val isVortexShaderSupported: Boolean = true

// Vortex2 (Galaxy) Shader
private const val VORTEX2_SHADER_AGSL = """
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

fun Modifier.vortex2Effect(isDark: Boolean): Modifier = runtimeShader(VORTEX2_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isVortex2ShaderSupported: Boolean = true


// ==============================================================================
// 2. CREATIVE SHADERS (Second Generation)
// ==============================================================================

// Silk Shader
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

fun Modifier.silkEffect(isDark: Boolean): Modifier = runtimeShader(SILK_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isSilkShaderSupported: Boolean = true


// Mosaic Shader
private const val MOSAIC_SHADER_AGSL = """
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

val isMosaicShaderSupported: Boolean = true


// Cyber Shader
private const val CYBER_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    const float PI = 3.14159265359;
    
    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
        
        float t = iTime * 0.05 * PI;
        float2 gridUV = uv * 2.0;
        
        float angle = sin(t * 0.1) * 0.1;
        float s = sin(angle);
        float c = cos(angle);
        gridUV = float2(gridUV.x * c - gridUV.y * s, gridUV.x * s + gridUV.y * c);
        
        gridUV.y -= t * 0.2; 
        
        float2 grid = abs(fract(gridUV) - 0.5);
        float line = min(grid.x, grid.y);
        
        float lineWidth = 0.05 * (1.0 - uv.y * 0.6); 
        float gridIntensity = 1.0 - smoothstep(0.0, lineWidth, line);
        gridIntensity *= (1.0 - smoothstep(0.0, 0.8, uv.y));
        
        half3 cGrid = half3(0.0, 0.8, 1.0);     
        half3 cGlow = half3(0.8, 0.0, 1.0);     
        half3 cBg = half3(0.05, 0.02, 0.1);     
        
        half3 col = cBg;
        col += cGrid * gridIntensity * 0.5;
        
        float packet = 0.0;
        float hFlow = sin(gridUV.x * 2.0 + t * 2.0) * sin(gridUV.x * 5.0 - t * 3.0);
        if (grid.y < 0.1 && hFlow > 0.8) packet += 1.0;
        
        float vFlow = sin(gridUV.y * 2.0 - t * 2.0) * sin(gridUV.y * 5.0 + t * 3.0);
        if (grid.x < 0.1 && vFlow > 0.8) packet += 1.0;
        
        col += cGlow * packet * 0.8;
        col += half3(0.1, 0.0, 0.2) * (1.0 - uv.y) * 0.5;
        
        float vign = smoothstep(1.5, 0.5, length(uv));
        col *= vign;
        col *= 0.9 + 0.1 * sin(uv.y * 200.0 + t * 10.0);

        return half4(col, 1.0);
    }
"""

fun Modifier.cyberEffect(isDark: Boolean): Modifier = runtimeShader(CYBER_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isCyberShaderSupported: Boolean = true


// ==============================================================================
// 3. ADVANCED SHADERS (Third Generation)
// ==============================================================================

// Plasma Shader
private const val PLASMA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;
    
    mat2 rot(float a) {
        float c = cos(a), s = sin(a);
        return mat2(c, s, -s, c);
    }
    
    const mat3 m3 = mat3(0.33338, 0.56034, -0.71817, -0.87887, 0.32651, -0.15323, 0.15162, 0.69596, 0.61339) * 1.93;
    
    float mag2(float2 p) { return dot(p, p); }
    
    float2 disp(float t) {
        return float2(sin(t * 0.22) * 1.0, cos(t * 0.175) * 1.0) * 2.0;
    }
    
    float2 map(float3 p, float t) {
        float3 p2 = p;
        p2.xy -= disp(p.z).xy;
        p.xy = rot(sin(p.z + t) * 0.1 + t * 0.09) * p.xy;
        
        float cl = mag2(p2.xy);
        float d = 0.0;
        p *= 0.61;
        float z = 1.0;
        float trk = 1.0;
        float dspAmp = 0.1;
        
        for(int i = 0; i < 4; i++) {
            p += sin(p.zxy * 0.75 * trk + t * trk * 0.8) * dspAmp;
            d -= abs(dot(cos(p), sin(p.yzx)) * z);
            z *= 0.57;
            trk *= 1.4;
            p = m3 * p;
        }
        
        d = abs(d + 4.0) - 2.5;
        return float2(d + cl * 0.2 + 0.25, cl);
    }
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float2 p = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float time = iTime * 0.15; 
        
        float3 ro = float3(0.0, 0.0, time);
        ro += float3(sin(iTime) * 0.5, 0.0, 0.0);
        
        float dspAmp = 0.85;
        ro.xy += disp(ro.z).xy * dspAmp;
        float tgtDst = 3.5;
        
        float3 target = normalize(ro - float3(disp(time + tgtDst).xy * dspAmp, time + tgtDst));
        float3 rightdir = normalize(cross(target, float3(0, 1, 0)));
        float3 updir = normalize(cross(rightdir, target));
        rightdir = normalize(cross(updir, target));
        float3 rd = normalize((p.x * rightdir + p.y * updir) * 1.0 - target);
        
        rd.xy = rot(-disp(time + 3.5).x * 0.2) * rd.xy;
        
        float4 rez = float4(0.0);
        float t = 1.5;
        
        for(int i = 0; i < 40; i++) {
            if(rez.a > 0.99) break;
            
            float2 res = map(ro + rd * t, time);
            float d = max(res.x, 0.01);
            float colID = res.y; 
            
            if(d < 0.05) { 
                float c = 0.5 * (1.0 - clamp(d * 20.0, 0.0, 1.0));
                rez.rgb += (1.0 - rez.a) * half3(c * 0.8, c * 0.5, c * 1.0);
                rez.a += (1.0 - rez.a) * c;
            }
            t += d;
        }
        
        return half4(rez.rgb, 1.0);
    }
"""

fun Modifier.plasmaEffect(isDark: Boolean): Modifier = runtimeShader(PLASMA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isPlasmaShaderSupported: Boolean = true

// Fireflies (Bokeh) Shader
private const val FIREFLIES_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.5;
        half3 col = half3(0.0);
        
        for(float i=0.0; i<20.0; i++) {
            float z = fract(i * 0.13 + t * 0.1);
            float size = mix(15.0, 0.5, z);
            float fade = smoothstep(0.0, 0.3, z) * smoothstep(1.0, 0.7, z);
            
            float2 pos = float2(
                sin(t * 0.5 + i * 2.5) * 0.8,
                cos(t * 0.3 + i * 1.5) * 0.8
            ) * (1.0 - z);
            
            float d = length(uv - pos);
            float circle = smoothstep(0.05 * size, 0.0, d);
            col += half3(1.0, 0.9, 0.5) * circle * fade * 0.05;
        }
        
        return half4(col, 1.0);
    }
"""

fun Modifier.firefliesEffect(isDark: Boolean): Modifier = runtimeShader(FIREFLIES_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isFirefliesShaderSupported: Boolean = true

// Cosmic (Galaxy) Shader
private const val COSMIC_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.2;
        float len = length(uv);
        float angle = atan(uv.y, uv.x);
        
        float spiral = sin(angle * 3.0 + len * 10.0 - t * 5.0);
        float intensity = smoothstep(0.8, 1.0, spiral) * smoothstep(0.5, 0.0, abs(len - 0.6));
        
        half3 col = half3(intensity * 0.5, intensity * 0.2, intensity * 0.8); // Purple/Blue
        col += half3(0.05, 0.0, 0.1); // Background
        
        return half4(col, 1.0);
    }
"""

fun Modifier.cosmicEffect(isDark: Boolean): Modifier = runtimeShader(COSMIC_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isCosmicShaderSupported: Boolean = true

// ==============================================================================
// 4. NEW GENERATION (Fourth Generation)
// ==============================================================================

// Matrix Shader
private const val MATRIX_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float2 grid = float2(30.0, 15.0); // Columns, Rows
        float2 cellUV = fract(uv * grid);
        float2 cellId = floor(uv * grid);
        
        float t = iTime * 0.5 + random(float2(cellId.x, 0.0)) * 10.0;
        float drop = mod(cellId.y + t, grid.y);
        float trail = smoothstep(0.0, 5.0, drop); // Trail length
        float lead = step(drop, 1.0); // Bright lead character
        
        float charShape = step(0.5, random(cellUV + floor(t))); // Random flashing pixels
        
        half3 col = half3(0.0, 1.0, 0.2) * (trail * 0.5 + lead) * charShape;
        return half4(col, 1.0);
    }
"""

fun Modifier.matrixEffect(isDark: Boolean): Modifier = runtimeShader(MATRIX_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isMatrixShaderSupported: Boolean = true

// DNA Shader
private const val DNA_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.2;
        
        float helix1 = sin(uv.y * 10.0 + t);
        float helix2 = sin(uv.y * 10.0 + t + 3.14159);
        
        float strand1 = smoothstep(0.05, 0.0, abs(uv.x - helix1 * 0.3));
        float strand2 = smoothstep(0.05, 0.0, abs(uv.x - helix2 * 0.3));
        
        // Ladder rungs
        float rungs = step(0.9, sin(uv.y * 50.0)) * step(abs(uv.x), abs(helix1 * 0.3));
        
        half3 col = half3(0.0);
        col += half3(0.2, 0.6, 1.0) * strand1;
        col += half3(1.0, 0.2, 0.6) * strand2;
        col += half3(0.8) * rungs * 0.5;
        
        return half4(col, 1.0);
    }
"""

fun Modifier.dnaEffect(isDark: Boolean): Modifier = runtimeShader(DNA_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isDnaShaderSupported: Boolean = true

// Black Hole Shader
private const val BLACKHOLE_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float dist = length(uv);
        float eventHorizon = 0.2;
        
        // Accretion Disk
        float angle = atan(uv.y, uv.x);
        float t = iTime * 0.4;
        float disk = smoothstep(0.0, 1.0, sin(angle * 2.0 + 10.0/dist - t));
        float diskShape = smoothstep(0.6, 0.2, dist) * smoothstep(eventHorizon, 0.2, dist);
        
        // Lensing
        float lens = smoothstep(0.5, 0.0, dist);
        
        half3 col = half3(1.0, 0.6, 0.1) * disk * diskShape * 2.0; // Orange disk
        col += half3(0.05) * lens; 
        
        if (dist < eventHorizon) col = half3(0.0); // Black hole center
        
        return half4(col, 1.0);
    }
"""

fun Modifier.blackHoleEffect(isDark: Boolean): Modifier = runtimeShader(BLACKHOLE_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isBlackHoleShaderSupported: Boolean = true

// Topography Shader
private const val TOPOGRAPHY_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    float random(float2 st) { return fract(sin(dot(st.xy, float2(12.9898,78.233))) * 43758.5453123); }
    float noise(float2 st) {
        float2 i = floor(st);
        float2 f = fract(st);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(random(i), random(i+float2(1,0)), f.x), mix(random(i+float2(0,1)), random(i+float2(1,1)), f.x), f.y);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float n = noise(uv * 5.0 + iTime * 0.1);
        float lines = sin(n * 50.0);
        float topo = smoothstep(0.9, 1.0, lines);
        
        half3 col = half3(0.1, 0.15, 0.2) + half3(0.0, 0.8, 0.6) * topo;
        return half4(col, 1.0);
    }
"""

fun Modifier.topographyEffect(isDark: Boolean): Modifier = runtimeShader(TOPOGRAPHY_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}

val isTopographyShaderSupported: Boolean = true
