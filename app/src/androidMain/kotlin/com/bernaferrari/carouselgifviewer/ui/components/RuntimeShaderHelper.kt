package com.bernaferrari.carouselgifviewer.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class AndroidShaderUniformProvider(val shader: RuntimeShader) : ShaderUniformProvider {
    override fun setFloatUniform(name: String, value: Float) {
        shader.setFloatUniform(name, value)
    }

    override fun setFloatUniform(name: String, value1: Float, value2: Float) {
        shader.setFloatUniform(name, value1, value2)
    }

    override fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float) {
        shader.setFloatUniform(name, value1, value2, value3)
    }

    override fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {
        shader.setFloatUniform(name, value1, value2, value3, value4)
    }
}

actual fun Modifier.runtimeShader(
    shaderSkSl: String,
    uniforms: ShaderUniformProvider.(time: Float, width: Float, height: Float) -> Unit
): Modifier = composed {
    if (Build.VERSION.SDK_INT >= 33) {
        var time by remember { mutableStateOf(0f) }
        
        // Universal animation loop
        LaunchedEffect(Unit) {
            val startTime = withInfiniteAnimationFrameMillis { it }
            while (true) {
                withInfiniteAnimationFrameMillis { frameTime ->
                    time = (frameTime - startTime) / 1000f
                }
            }
        }
        
        val shader = remember(shaderSkSl) { RuntimeShader(shaderSkSl) }
        val brush = remember(shader) { ShaderBrush(shader) }
        val provider = remember(shader) { AndroidShaderUniformProvider(shader) }
        
        this.drawWithCache {
            onDrawBehind {
                provider.uniforms(time, size.width, size.height)
                drawRect(brush)
            }
        }
    } else this
}
