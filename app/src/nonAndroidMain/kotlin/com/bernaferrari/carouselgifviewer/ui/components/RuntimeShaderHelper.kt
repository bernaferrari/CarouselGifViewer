package com.bernaferrari.carouselgifviewer.ui.components

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
import androidx.compose.ui.graphics.asComposeShader
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

private class SkiaShaderUniformProvider(val builder: RuntimeShaderBuilder) : ShaderUniformProvider {
    override fun setFloatUniform(name: String, value: Float) {
        builder.uniform(name, value)
    }

    override fun setFloatUniform(name: String, value1: Float, value2: Float) {
        builder.uniform(name, value1, value2)
    }
    
    override fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float) {
        builder.uniform(name, value1, value2, value3)
    }

    override fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {
        builder.uniform(name, value1, value2, value3, value4)
    }
}

actual fun Modifier.runtimeShader(
    shaderSkSl: String,
    uniforms: ShaderUniformProvider.(time: Float, width: Float, height: Float) -> Unit
): Modifier = composed {
    var time by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        val startTime = withInfiniteAnimationFrameMillis { it }
        while (true) {
            withInfiniteAnimationFrameMillis { frameTime ->
                time = (frameTime - startTime) / 1000f
            }
        }
    }
    
    val runtimeEffect = remember(shaderSkSl) { RuntimeEffect.makeForShader(shaderSkSl) }
    
    this.drawWithCache {
        onDrawBehind {
            val builder = RuntimeShaderBuilder(runtimeEffect)
            val provider = SkiaShaderUniformProvider(builder)
            provider.uniforms(time, size.width, size.height)
            
            val shader = builder.makeShader()
            val brush = ShaderBrush(shader.asComposeShader())
            
            drawRect(brush)
        }
    }
}
