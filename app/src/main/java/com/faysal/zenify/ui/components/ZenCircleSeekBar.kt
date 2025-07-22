package com.faysal.zenify.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.Nunito
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * A custom mirrored waveform seek bar for music visualization.
 *
 * @param modifier Modifier to apply layout changes
 * @param waveHeights List of normalized (0f..1f) wave heights
 * @param activeWaveColor Color of active progress waveform
 * @param inactiveWaveColor Color of inactive (remaining) waveform
 * @param barWidth Width of each waveform bar
 * @param barSpacing Spacing between bars
 * @param maxBarHeight Maximum height of waveform bars (extends both up and down)
 */
@Composable
fun ZenCircleSeekBar(
    modifier: Modifier = Modifier,
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    strokeWidth: Dp = 8.dp,
    radius: Dp = 100.dp,
    progressColor: Color = Color(0xFF8B5CF6),
    backgroundColor: Color = Color.Gray.copy(alpha = 0.3f),
    thumbColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val density = LocalDensity.current
    val strokePx = with(density) { strokeWidth.toPx() }
    val radiusPx = with(density) { radius.toPx() }

    var isDragging by remember { mutableStateOf(false) }
    var dragAngle by remember { mutableFloatStateOf(0f) }

    val progress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragAngle / 360f else progress,
        animationSpec = tween(200),
        label = "ZenCircleProgress"
    )

    val currentMs = (animatedProgress * durationMs).toLong().coerceAtMost(durationMs)
    val remainingMs = (durationMs - currentMs).coerceAtLeast(0)

    Box(
        modifier = modifier
            .size(radius * 2 + strokeWidth * 2)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        onSeek(((dragAngle / 360f) * durationMs).toLong())
                    },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, _ ->
                        val boxCenter = Offset((size.width / 2).toFloat(),
                            (size.height / 2).toFloat()
                        )
                        val x = change.position.x - boxCenter.x
                        val y = boxCenter.y - change.position.y
                        val angle = (atan2(y, x) * 180 / PI).toFloat()
                        dragAngle = ((angle + 360) % 360f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            drawCircle(
                color = backgroundColor,
                radius = radiusPx,
                center = center,
                style = Stroke(strokePx, cap = StrokeCap.Round)
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(strokePx, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radiusPx, center.y - radiusPx),
                size = Size(radiusPx * 2, radiusPx * 2)
            )

            val angleRad = Math.toRadians((animatedProgress * 360 - 90).toDouble())
            val thumbX = center.x + cos(angleRad).toFloat() * radiusPx
            val thumbY = center.y + sin(angleRad).toFloat() * radiusPx

            drawCircle(
                color = thumbColor,
                radius = strokePx * 0.7f,
                center = Offset(thumbX, thumbY)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(currentMs),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = Nunito
            )
            Text(
                text = "-${formatTime(remainingMs)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontFamily = Nunito
            )
        }
    }
}




/*@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun ZenCircleSeekBarPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZenWaveSeekBar(
            modifier = Modifier.fillMaxWidth(),
            currentPositionMs = 34500,
            durationMs = 99567,
            onSeek = {  }
        )
    }
}*/
