package com.bernaferrari.carouselgifviewer.ui.components.shaders

import androidx.compose.ui.Modifier
import com.bernaferrari.carouselgifviewer.ui.components.runtimeShader

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
