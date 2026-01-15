package com.bernaferrari.carouselgifviewer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A serene, aurora-inspired animated background with soft flowing gradients.
 * Features gentle mesh-like gradient blobs that slowly drift and morph,
 * creating a calming, premium visual effect.
 */
@Composable
fun ShaderBackground(
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (!enabled) return

    val isDark = isSystemInDarkTheme()
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val surface = MaterialTheme.colorScheme.surface

    val infiniteTransition = rememberInfiniteTransition(label = "auroraBg")

    // Slow, smooth animation phases for organic movement
    // Using Reverse mode ensures seamless looping without visible jumps
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase2"
    )

    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase3"
    )

    val phase4 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase4"
    )

    // Gentle breathing effect for overall luminosity
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Pre-calculate aurora colors with appropriate alpha for the theme
    val auroraColors = remember(primary, secondary, tertiary, isDark) {
        AuroraColors(
            // Soft, dreamy accent colors - increased alpha for visibility
            accent1 = if (isDark) {
                primary.copy(alpha = 0.65f)
            } else {
                primary.copy(alpha = 0.50f)
            },
            accent2 = if (isDark) {
                secondary.copy(alpha = 0.55f)
            } else {
                secondary.copy(alpha = 0.45f)
            },
            accent3 = if (isDark) {
                tertiary.copy(alpha = 0.50f)
            } else {
                tertiary.copy(alpha = 0.40f)
            },
            // Subtle complementary tones
            glow1 = if (isDark) {
                primary.copy(alpha = 0.35f)
            } else {
                primary.copy(alpha = 0.25f)
            },
            glow2 = if (isDark) {
                secondary.copy(alpha = 0.30f)
            } else {
                secondary.copy(alpha = 0.20f)
            },
            surface = surface
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Base surface
        drawRect(color = auroraColors.surface)

        // === Large, soft aurora blobs ===
        // These create the main visual interest with smooth, organic movement

        // Top-left aurora blob
        val blob1Center = Offset(
            x = width * (0.2f + 0.15f * sin(phase1)),
            y = height * (0.25f + 0.12f * cos(phase2 * 0.7f))
        )
        drawAuroraBlob(
            center = blob1Center,
            radiusX = width * 0.55f * breathe,
            radiusY = height * 0.45f * breathe,
            color = auroraColors.accent1,
            rotation = phase1 * 0.3f
        )

        // Top-right aurora blob
        val blob2Center = Offset(
            x = width * (0.75f + 0.1f * cos(phase2)),
            y = height * (0.20f + 0.15f * sin(phase1 * 0.8f))
        )
        drawAuroraBlob(
            center = blob2Center,
            radiusX = width * 0.5f * breathe,
            radiusY = height * 0.4f * breathe,
            color = auroraColors.accent2,
            rotation = -phase2 * 0.25f
        )

        // Bottom aurora blob - uses secondary color for harmony, gentler movement
        val blob3Center = Offset(
            x = width * (0.5f + 0.06f * sin(phase4 * 0.5f)),  // Slower, smaller horizontal drift
            y = height * (0.85f + 0.04f * cos(phase3 * 0.4f)) // Subtle vertical movement
        )
        drawAuroraBlob(
            center = blob3Center,
            radiusX = width * 0.55f * breathe,
            radiusY = height * 0.35f * breathe,
            color = auroraColors.glow1,  // Uses primary glow color - softer and more harmonious
            rotation = phase4 * 0.15f    // Very slow rotation
        )

        // === Subtle ambient glow on the right ===
        // Adds depth without being too prominent
        val glow2Center = Offset(
            x = width * 0.9f + cos(phase2) * width * 0.04f,
            y = height * 0.5f + sin(phase1) * height * 0.05f
        )
        drawSoftGlow(
            center = glow2Center,
            radius = minOf(width, height) * 0.35f,
            color = auroraColors.glow2
        )

        // === Gentle shimmer highlights ===
        // Very subtle light spots that add a touch of magic
        drawShimmerHighlights(
            phase1 = phase1,
            phase2 = phase2,
            phase3 = phase3,
            baseAlpha = if (isDark) 0.06f else 0.04f
        )
    }
}

private data class AuroraColors(
    val accent1: Color,
    val accent2: Color,
    val accent3: Color,
    val glow1: Color,
    val glow2: Color,
    val surface: Color
)

/**
 * Draws a soft, elliptical aurora blob with smooth gradient falloff.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAuroraBlob(
    center: Offset,
    radiusX: Float,
    radiusY: Float,
    color: Color,
    rotation: Float
) {
    // Use the average radius for the radial gradient (approximating the ellipse)
    val avgRadius = (radiusX + radiusY) / 2f
    
    // Apply subtle rotation offset to center position for organic movement
    val rotatedCenter = Offset(
        x = center.x + sin(rotation) * avgRadius * 0.05f,
        y = center.y + cos(rotation) * avgRadius * 0.03f
    )

    val gradient = Brush.radialGradient(
        colorStops = arrayOf(
            0.0f to color,
            0.3f to color.copy(alpha = color.alpha * 0.7f),
            0.6f to color.copy(alpha = color.alpha * 0.3f),
            0.85f to color.copy(alpha = color.alpha * 0.1f),
            1.0f to Color.Transparent
        ),
        center = rotatedCenter,
        radius = avgRadius
    )

    drawCircle(
        brush = gradient,
        radius = avgRadius,
        center = rotatedCenter
    )
}

/**
 * Draws a simple soft glow effect.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSoftGlow(
    center: Offset,
    radius: Float,
    color: Color
) {
    val gradient = Brush.radialGradient(
        colorStops = arrayOf(
            0.0f to color,
            0.5f to color.copy(alpha = color.alpha * 0.4f),
            1.0f to Color.Transparent
        ),
        center = center,
        radius = radius
    )

    drawCircle(
        brush = gradient,
        radius = radius,
        center = center
    )
}

/**
 * Draws subtle shimmer highlights that gently sparkle.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawShimmerHighlights(
    phase1: Float,
    phase2: Float,
    phase3: Float,
    baseAlpha: Float
) {
    val width = size.width
    val height = size.height

    // Define a few shimmer points that move gently
    val shimmerPoints = listOf(
        ShimmerPoint(
            baseX = 0.3f,
            baseY = 0.2f,
            phaseMultiplierX = 1.0f,
            phaseMultiplierY = 0.7f,
            size = 0.08f
        ),
        ShimmerPoint(
            baseX = 0.7f,
            baseY = 0.35f,
            phaseMultiplierX = 0.8f,
            phaseMultiplierY = 1.2f,
            size = 0.06f
        ),
        ShimmerPoint(
            baseX = 0.5f,
            baseY = 0.55f,
            phaseMultiplierX = 1.1f,
            phaseMultiplierY = 0.9f,
            size = 0.07f
        ),
        ShimmerPoint(
            baseX = 0.2f,
            baseY = 0.75f,
            phaseMultiplierX = 0.9f,
            phaseMultiplierY = 1.0f,
            size = 0.05f
        ),
        ShimmerPoint(
            baseX = 0.8f,
            baseY = 0.7f,
            phaseMultiplierX = 1.2f,
            phaseMultiplierY = 0.8f,
            size = 0.055f
        )
    )

    shimmerPoints.forEachIndexed { index, point ->
        // Calculate position with gentle oscillation
        val x = width * (point.baseX + 0.05f * sin(phase1 * point.phaseMultiplierX + index))
        val y = height * (point.baseY + 0.04f * cos(phase2 * point.phaseMultiplierY + index * 0.7f))
        
        // Calculate alpha with pulsing effect
        val pulseAlpha = baseAlpha * (0.5f + 0.5f * sin(phase3 + index * PI.toFloat() / 3f))
        
        val radius = minOf(width, height) * point.size

        val gradient = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color.White.copy(alpha = pulseAlpha),
                0.4f to Color.White.copy(alpha = pulseAlpha * 0.3f),
                1.0f to Color.Transparent
            ),
            center = Offset(x, y),
            radius = radius
        )

        drawCircle(
            brush = gradient,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

private data class ShimmerPoint(
    val baseX: Float,
    val baseY: Float,
    val phaseMultiplierX: Float,
    val phaseMultiplierY: Float,
    val size: Float
)
