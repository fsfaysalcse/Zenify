package com.faysal.zenify.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.faysal.zenify.ui.theme.AnimateCircle
import com.faysal.zenify.ui.theme.ZenifyPrimary

@Composable
fun AnimatedBackgroundCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "circles")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(20) { index ->
            val alpha = (0.1f + (index % 5) * 0.02f)
            val radius = (50 + index * 30).dp.toPx()
            val centerX = size.width * (0.2f + (index % 3) * 0.3f)
            val centerY = size.height * (0.1f + (index % 4) * 0.25f)

            drawCircle(
                color = AnimateCircle.copy(alpha = alpha),
                radius = radius * pulseScale,
                center = Offset(centerX, centerY)
            )
        }
    }
}
