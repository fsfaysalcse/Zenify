package com.faysal.zenify.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun MusicPlayerHomeBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "home_bg")

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val audioWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "audioWave"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF0A0A0A),
                        Color(0xFF050505)
                    ),
                    radius = 1200f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFloatingOrbs(pulseScale, waveOffset)
            drawVinylRecords(vinylRotation)
            drawFloatingNotes(waveOffset)
        }
        content()
    }
}

@Composable
fun NavigationDrawerBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "drawer_bg")

    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )

    val particleFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    val meshRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mesh"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF0D0D0D),
                        Color(0xFF060606)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFlowingLines(flowOffset)
            drawGeometricMesh(meshRotation)
            drawFloatingParticles(particleFloat)
        }
        content()
    }
}

private fun DrawScope.drawAudioSpectrum(beat: Float, pulseScale: Float) {
    val barCount = 30
    val barWidth = size.width / barCount
    val maxHeight = size.height * 0.6f

    val spectrumColors = listOf(
        Color(0xFF6366F1).copy(alpha = 0.8f),
        Color(0xFF8B5CF6).copy(alpha = 0.7f),
        Color(0xFFEC4899).copy(alpha = 0.6f)
    )

    repeat(barCount) { i ->
        val x = i * barWidth + barWidth * 0.5f
        val frequency = 0.15f + i * 0.03f
        val height = maxHeight * (0.1f + 0.9f * (sin(beat + i * frequency) * 0.5f + 0.5f)) * pulseScale

        val colorIndex = i % spectrumColors.size
        val color = spectrumColors[colorIndex]

        val startY = size.height * 0.9f

        drawRect(
            color = color,
            topLeft = Offset(x - barWidth * 0.3f, startY - height),
            size = androidx.compose.ui.geometry.Size(barWidth * 0.6f, height)
        )
    }
}

private fun DrawScope.drawFloatingOrbs(pulseScale: Float, offset: Float) {
    val orbPositions = listOf(
        Offset(size.width * 0.2f, size.height * 0.3f),
        Offset(size.width * 0.8f, size.height * 0.5f),
        Offset(size.width * 0.5f, size.height * 0.2f)
    )

    val orbColors = listOf(
        Color(0xFF6366F1).copy(alpha = 0.6f),
        Color(0xFFEC4899).copy(alpha = 0.5f),
        Color(0xFF10B981).copy(alpha = 0.4f)
    )

    orbPositions.forEachIndexed { index, basePosition ->
        val floatX = basePosition.x + cos(offset + index * 2f) * 30f
        val floatY = basePosition.y + sin(offset + index * 1.5f) * 20f
        val position = Offset(floatX, floatY)

        val dynamicScale = pulseScale + sin(offset + index * 2f) * 0.2f
        val radius = (40f + index * 15f) * dynamicScale

        // Outer glow
        drawCircle(
            color = orbColors[index].copy(alpha = 0.2f),
            radius = radius * 1.5f,
            center = position
        )

        // Main orb
        drawCircle(
            color = orbColors[index],
            radius = radius,
            center = position
        )
    }
}

private fun DrawScope.drawVinylRecords(rotation: Float) {
    val colors = listOf(
        Color(0xFF1F1F1F).copy(alpha = 0.3f),
        Color(0xFF2F2F2F).copy(alpha = 0.25f),
        Color(0xFF3F3F3F).copy(alpha = 0.2f)
    )

    repeat(3) { i ->
        val centerX = size.width * (0.2f + i * 0.3f)
        val centerY = size.height * (0.15f + i * 0.25f)
        val radius = (80f + i * 30f)

        rotate(rotation + i * 45f, pivot = Offset(centerX, centerY)) {
            repeat(5) { ring ->
                drawCircle(
                    color = colors[i],
                    radius = radius - ring * 15f,
                    center = Offset(centerX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }

            drawCircle(
                color = Color(0xFF6366F1).copy(alpha = 0.4f),
                radius = 8f,
                center = Offset(centerX, centerY)
            )
        }
    }
}

private fun DrawScope.drawFloatingNotes(offset: Float) {
    val noteColor = Color(0xFF6366F1).copy(alpha = 0.5f)

    repeat(8) { i ->
        val x = size.width * (0.15f + (i % 4) * 0.25f)
        val baseY = size.height * (0.3f + (i % 3) * 0.2f)
        val floatY = baseY + sin(offset + i * 1.2f) * 40f
        val noteSize = 12f + (i % 3) * 6f

        drawPath(
            path = createMusicNotePath(Offset(x, floatY), noteSize),
            color = noteColor
        )
    }
}

private fun DrawScope.drawFlowingLines(offset: Float) {
    val primary = Color(0xFF6366F1).copy(alpha = 0.25f)
    val secondary = Color(0xFF8B5CF6).copy(alpha = 0.2f)

    repeat(6) { i ->
        val path = Path().apply {
            val startY = size.height * (0.15f + i * 0.15f)
            val amplitude = 80f + i * 20f
            val frequency = 0.008f + i * 0.002f

            moveTo(-100f, startY)

            var x = -100f
            while (x <= size.width + 100f) {
                val y = startY + sin((x + offset * 300f) * frequency) * amplitude
                lineTo(x, y)
                x += 10f
            }
        }

        drawPath(
            path = path,
            color = if (i % 2 == 0) primary else secondary,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = (2f + i * 0.5f).dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(20f, 10f)
                )
            )
        )
    }
}

private fun DrawScope.drawGeometricMesh(rotation: Float) {
    val meshColor = Color(0xFF6366F1).copy(alpha = 0.12f)
    val gridSize = 60f

    rotate(rotation, pivot = size.center) {
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = meshColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx()
            )
            x += gridSize
        }

        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = meshColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
            y += gridSize
        }
    }
}

private fun DrawScope.drawFloatingParticles(offset: Float) {
    val particleColors = listOf(
        Color(0xFF6366F1).copy(alpha = 0.3f),
        Color(0xFF8B5CF6).copy(alpha = 0.25f),
        Color(0xFFEC4899).copy(alpha = 0.2f)
    )

    repeat(15) { i ->
        val baseX = size.width * ((i % 5) * 0.2f)
        val baseY = size.height * ((i % 3) * 0.33f)

        val floatX = baseX + cos(offset + i * 0.8f) * 50f
        val floatY = baseY + sin(offset * 1.2f + i * 1.5f) * 80f

        val particleSize = 4f + (i % 4) * 3f
        val color = particleColors[i % particleColors.size]

        when (i % 4) {
            0 -> drawCircle(color, particleSize, Offset(floatX, floatY))
            1 -> drawRect(
                color,
                topLeft = Offset(floatX - particleSize, floatY - particleSize),
                size = androidx.compose.ui.geometry.Size(particleSize * 2, particleSize * 2)
            )
            2 -> {
                val path = Path().apply {
                    moveTo(floatX, floatY - particleSize)
                    lineTo(floatX + particleSize, floatY + particleSize)
                    lineTo(floatX - particleSize, floatY + particleSize)
                    close()
                }
                drawPath(path, color)
            }
            else -> {
                drawLine(
                    color,
                    Offset(floatX - particleSize, floatY),
                    Offset(floatX + particleSize, floatY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}

private fun createMusicNotePath(center: Offset, size: Float): Path {
    return Path().apply {
        val noteX = center.x
        val noteY = center.y

        addOval(
            androidx.compose.ui.geometry.Rect(
                noteX - size * 0.3f,
                noteY - size * 0.2f,
                noteX + size * 0.3f,
                noteY + size * 0.2f
            )
        )

        moveTo(noteX + size * 0.25f, noteY)
        lineTo(noteX + size * 0.25f, noteY - size * 1.2f)

        addOval(
            androidx.compose.ui.geometry.Rect(
                noteX + size * 0.1f,
                noteY - size * 1.4f,
                noteX + size * 0.6f,
                noteY - size * 1.0f
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MusicPlayerHomeBackgroundPreview() {
    MaterialTheme {
        MusicPlayerHomeBackground {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavigationDrawerBackgroundPreview() {
    MaterialTheme {
        NavigationDrawerBackground {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}