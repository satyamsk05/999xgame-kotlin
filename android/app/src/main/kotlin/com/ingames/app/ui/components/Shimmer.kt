package com.ingames.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.shimmerBackground(
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFF2A0835),
        Color(0xFF4A105C),
        Color(0xFF2A0835)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    return this
        .background(brush = brush, shape = shape)
}

@Composable
fun ShimmerCardSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 150.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shimmerBackground(RoundedCornerShape(14.dp))
    )
}
