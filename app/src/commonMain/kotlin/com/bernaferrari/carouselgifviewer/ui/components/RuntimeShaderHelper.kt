package com.bernaferrari.carouselgifviewer.ui.components

import androidx.compose.ui.Modifier

interface ShaderUniformProvider {
    fun setFloatUniform(name: String, value: Float)
    fun setFloatUniform(name: String, value1: Float, value2: Float)
    fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float)
    fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float)
}

expect fun Modifier.runtimeShader(
    shaderSkSl: String,
    uniforms: ShaderUniformProvider.(time: Float, width: Float, height: Float) -> Unit
): Modifier
