package com.faysal.zenify.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2


/**
 * RadialProgressBar is a customizable and draggable circular progress bar with:
 *
 * - Gradient arc showing current progress
 * - Background ring and 12 o'clock marker
 * - User can drag around the ring to change progress
 *
 * @param modifier Modifier for layout and styling
 * @param progress Current progress value (0f to 1f)
 * @param onProgressChange Callback to update progress based on user drag
 * @param sizeDp Total size of the circular widget
 * @param radiusDp Radius of the progress arc
 * @param strokeDp Stroke width of the arc
 * @param colors Gradient colors of the arc
 */

@Composable
fun RadialProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    sizeDp: Dp = 320.dp,
    radiusDp: Dp = 120.dp,
    strokeDp: Dp = 5.dp,
    colors: List<Color> = listOf(
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4),
        Color(0xFF10B981)
    )
) {
    val density = LocalDensity.current
    val pxSize = with(density) { sizeDp.toPx() }
    val ringRadius = with(density) { radiusDp.toPx() }
    val strokeWidth = with(density) { strokeDp.toPx() }

    var internalProgress by remember { mutableStateOf(progress.coerceIn(0f, 1f)) }

    LaunchedEffect(progress) {
        if (progress != internalProgress) {
            internalProgress = progress.coerceIn(0f, 1f)
        }
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val center = Offset(pxSize / 2f, pxSize / 2f)
                        val position = change.position
                        val angle = (atan2(center.y - position.y, position.x - center.x) * 180f / PI).toFloat()
                        val adjustedAngle = ((angle + 360f + 90f) % 360f)
                        val newProgress = (adjustedAngle / 360f).coerceIn(0f, 1f)
                        internalProgress = newProgress
                        onProgressChange(newProgress)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            drawArc(
                brush = Brush.linearGradient(colors),
                startAngle = -90f,
                sweepAngle = 360f * internalProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(ringRadius * 2, ringRadius * 2),
                topLeft = Offset(center.x - ringRadius, center.y - ringRadius)
            )
        }
    }
}



@Preview
@Composable
fun GradientCircularProgressRingPreview() {
    RadialProgressBar(
        progress = 0.75f,
        onProgressChange = {}
    )
}