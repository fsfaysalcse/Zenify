package com.faysal.zenify.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.faysal.zenify.ui.theme.Nunito
import kotlin.random.Random

/**
 * A custom mirrored waveform seek bar for music visualization.
 *
 * @param modifier Modifier to apply layout changes
 * @param progress Current progress (0f to 1f)
 * @param onProgressChange Callback when user taps or drags to change progress
 * @param waveHeights List of normalized (0f..1f) wave heights
 * @param activeWaveColor Color of active progress waveform
 * @param inactiveWaveColor Color of inactive (remaining) waveform
 * @param barWidth Width of each waveform bar
 * @param barSpacing Spacing between bars
 * @param maxBarHeight Maximum height of waveform bars (extends both up and down)
 */
@Composable
fun ZenWaveSeekBar(
    modifier: Modifier = Modifier,
    progress: Float,
    durationMs: Long, // total duration in milliseconds
    onProgressChange: (Float) -> Unit = {},
    waveHeights: List<Float> = remember { generateZenWaveHeights(150) },
    activeWaveColor: Color = Color(0xFF8B5CF6),
    inactiveWaveColor: Color = Color.Gray.copy(alpha = 0.3f),
    barWidth: Dp = 2.dp,
    barSpacing: Dp = 1.dp,
    maxBarHeight: Dp = 24.dp
) {
    val density = LocalDensity.current
    var canvasWidth by remember { mutableFloatStateOf(0f) }

    val totalBarWidth = with(density) { (barWidth + barSpacing).toPx() }
    val barWidthPx = with(density) { barWidth.toPx() }
    val maxBarHeightPx = with(density) { maxBarHeight.toPx() }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "ZenWaveAnimation"
    )

    val currentMs = (animatedProgress * durationMs).toLong().coerceAtMost(durationMs)
    val remainingMs = (durationMs - currentMs).coerceAtLeast(0)

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .height(maxBarHeight * 2)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newProgress = (offset.x / canvasWidth).coerceIn(0f, 1f)
                        onProgressChange(newProgress)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val newProgress = (change.position.x / canvasWidth).coerceIn(0f, 1f)
                        onProgressChange(newProgress)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasWidth = size.width
                val centerY = size.height / 2f
                val maxBars = (size.width / totalBarWidth).toInt()
                val visibleBars = minOf(maxBars, waveHeights.size)
                val progressX = animatedProgress * size.width

                for (i in 0 until visibleBars) {
                    val x = i * totalBarWidth + barWidthPx / 2f
                    val height = waveHeights[i].coerceIn(0f, 1f) * maxBarHeightPx
                    val isActive = x <= progressX
                    val color = if (isActive) activeWaveColor else inactiveWaveColor

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x - barWidthPx / 2f, centerY - height),
                        size = Size(barWidthPx, height * 2),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidthPx / 2f, barWidthPx / 2f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentMs),
                fontFamily = Nunito,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "-${formatTime(remainingMs)}",
                fontFamily = Nunito,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Formats a duration in milliseconds to a string in MM:SS format.
 *
 * @param ms Duration in milliseconds
 * @return Formatted time string
 */

fun formatTime(ms: Long): String {
    val totalSec = ms / 1000
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return "%02d:%02d".format(minutes, seconds)
}


/**
 * Generates a random list of normalized waveform heights (0f..1f)
 */
fun generateZenWaveHeights(count: Int): List<Float> {
    return List(count) { Random.nextFloat() }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun ZenWaveSeekBarPreview() {
    var progress by remember { mutableStateOf(0.3f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZenWaveSeekBar(
            modifier = Modifier.fillMaxWidth(),
            progress = progress,
            onProgressChange = { progress = it },
            activeWaveColor = Color(0xFFFFFFFF),
            inactiveWaveColor = Color.White.copy(alpha = 0.1f),
            durationMs = 210000L,
            barWidth = 5.dp,
            barSpacing = 2.dp,
            maxBarHeight = 20.dp
        )
    }
}
