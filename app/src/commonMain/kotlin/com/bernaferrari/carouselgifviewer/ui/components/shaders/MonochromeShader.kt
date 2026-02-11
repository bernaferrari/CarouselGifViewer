package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

// Return of the Obra Dinn (1-bit dither)
private const val OBRA_DINN_SHADER_AGSL = """
    uniform float iTime;
    uniform float2 iResolution;

    half4 main(float2 fragCoord) {
        float2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
        float t = iTime * 0.2;
        
        // Rotate
        float s = sin(t * 0.5);
        float c = cos(t * 0.5);
        uv = float2(uv.x * c - uv.y * s, uv.x * s + uv.y * c);
        
        // Scale/Zoom breath
        uv *= 1.0 + 0.2 * sin(t);
        
        // Scene: Stormy sea texture
        float wave = sin(uv.x * 10.0 + t * 5.0) + sin(uv.y * 10.0 - t * 3.0);
        float noise = fract(sin(dot(uv, float2(12.9898, 78.233))) * 43758.5453);
        
        // Complex pattern similar to user request logic (StarLayer structure adapted for dithering)
        // Let's make it more interesting than just sine waves
        float val = wave * 0.5 + 0.5;
        
        // Add some "Octgram" style complexity to the base value
        val += sin(length(uv) * 20.0 - t * 10.0) * 0.5;
        
        // Analytic derivative for edge detection (Manual fwidth)
        // Approximating gradient
        float dwdx = 10.0 * cos(uv.x * 10.0 + t * 5.0) * (1.0/iResolution.x); // approx
        float dwdy = 10.0 * cos(uv.y * 10.0 - t * 3.0) * (1.0/iResolution.y);
        
        float edge = (abs(dwdx) + abs(dwdy)) * 50.0; // Stronger edge
        val += edge;
        
        // 1-bit Dither threshold (Bayer 2x2)
        // Need screen space coord for stable dither mask
        float2 pos = floor(fragCoord);
        int x = int(mod(pos.x, 2.0));
        int y = int(mod(pos.y, 2.0));
        float limit = 0.0;
        if(x==0 && y==0) limit = 0.25;
        if(x==1 && y==1) limit = 0.50;
        if(x==1 && y==0) limit = 0.75;
        if(x==0 && y==1) limit = 1.00;
        
        float color = step(limit, val + noise * 0.1); 
        
        // Palette
        half3 cDark = half3(0.1, 0.15, 0.1); 
        half3 cLight = half3(0.9, 0.95, 0.8);
        
        half3 col = mix(cDark, cLight, color);

        return half4(col, 1.0);
    }
"""

fun Modifier.monochromeEffect(isDark: Boolean): Modifier = runtimeShader(OBRA_DINN_SHADER_AGSL) { time, w, h ->
    setFloatUniform("iTime", time)
    setFloatUniform("iResolution", w, h)
}