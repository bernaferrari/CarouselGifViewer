package com.bernaferrari.carouselgifviewer.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bernaferrari.carouselgifviewer.ui.theme.AppColors

/**
 * A card with gradient background and premium styling
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradient: Brush = AppColors.primaryGradient,
    shape: Shape = RoundedCornerShape(24.dp), // AppShapes.large
    elevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = AppColors.Purple40.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(gradient),
        content = content
    )
}

/**
 * A surface with glassmorphic (frosted glass) effect
 */
@Composable
fun GlassmorphicSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp), // AppShapes.medium
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            ),
        content = content
    )
}

/**
 * Animated icon button with scale press effect
 */
@Composable
fun AnimatedIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
    pressedBackgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    shape: Shape = CircleShape,
    size: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) pressedBackgroundColor else backgroundColor,
        animationSpec = tween(150),
        label = "buttonBgColor"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = shape,
                spotColor = AppColors.Purple40.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

/**
 * Shimmer loading placeholder for images
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp) // AppShapes.small
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceContainerHigh,
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.surfaceContainerHigh
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(shimmerProgress * 1000f - 300f, 0f),
                    end = Offset(shimmerProgress * 1000f + 300f, 300f)
                )
            )
    )
}

/**
 * Premium loading indicator with animated gradient
 */
@Composable
fun PremiumLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .scale(pulse)
            .graphicsLayer { rotationZ = rotation }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            AppColors.Purple60,
                            AppColors.Blue60,
                            AppColors.Coral60,
                            AppColors.Purple60
                        )
                    ),
                    shape = CircleShape
                )
        )
        // Inner cutout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
        )
    }
}

/**
 * Glow effect modifier for selected items
 */
@Composable
fun Modifier.glowEffect(
    color: Color = AppColors.Purple60,
    radius: Dp = 16.dp,
    enabled: Boolean = true
): Modifier {
    return if (enabled) {
        this.then(
            Modifier
                .shadow(
                    elevation = radius,
                    shape = RoundedCornerShape(16.dp), // AppShapes.medium
                    spotColor = color.copy(alpha = 0.6f),
                    ambientColor = color.copy(alpha = 0.3f)
                )
        )
    } else {
        this
    }
}
