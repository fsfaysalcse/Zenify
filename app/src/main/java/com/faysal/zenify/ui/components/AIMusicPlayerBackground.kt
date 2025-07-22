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
import kotlin.math.abs

@Composable
fun AIMusicPlayerBackground(
    primaryColor: Color = Color(0xFF6366F1),
    secondaryColor: Color = Color(0xFF8B5CF6),
    accentColor: Color = Color(0xFFEC4899),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_music_bg")

    val aiPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aiPulse"
    )

    val neuralFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neuralFlow"
    )

    val waveform by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveform"
    )

    val hologramRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hologram"
    )

    val dataStream by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dataStream"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF0A0A0A),
                        Color.Black
                    ),
                    radius = 1000f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAINeuralNetwork(neuralFlow, primaryColor, secondaryColor)
            drawHolographicCircles(hologramRotation, aiPulse, primaryColor, accentColor)
            drawFloatingDataNodes(dataStream, aiPulse, primaryColor, secondaryColor, accentColor)
            drawEnergyField(aiPulse, neuralFlow, primaryColor, secondaryColor)
            drawAIParticleSystem(dataStream, aiPulse, accentColor)
        }
        content()
    }
}

private fun DrawScope.drawAINeuralNetwork(flow: Float, primaryColor: Color, secondaryColor: Color) {
    val nodes = listOf(
        Offset(size.width * 0.2f, size.height * 0.3f),
        Offset(size.width * 0.5f, size.height * 0.2f),
        Offset(size.width * 0.8f, size.height * 0.4f),
        Offset(size.width * 0.3f, size.height * 0.6f),
        Offset(size.width * 0.7f, size.height * 0.7f),
        Offset(size.width * 0.6f, size.height * 0.5f)
    )

    // Draw connections between nodes
    for (i in nodes.indices) {
        for (j in i + 1 until nodes.size) {
            val start = nodes[i]
            val end = nodes[j]
            val distance = kotlin.math.sqrt(
                (end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y)
            )

            if (distance < 300f) {
                val alpha = (1f - distance / 300f) * (0.3f + 0.4f * sin(flow + i * 0.5f))
                val connectionColor = if (i % 2 == 0) primaryColor else secondaryColor

                drawLine(
                    color = connectionColor.copy(alpha = alpha),
                    start = start,
                    end = end,
                    strokeWidth = (2f + sin(flow + i) * 1f).dp.toPx()
                )
            }
        }
    }

    // Draw nodes
    nodes.forEachIndexed { index, node ->
        val nodeSize = 8f + sin(flow + index * 0.8f) * 4f
        val nodeColor = if (index % 2 == 0) primaryColor else secondaryColor

        // Outer glow
        drawCircle(
            color = nodeColor.copy(alpha = 0.3f),
            radius = nodeSize * 2f,
            center = node
        )

        // Main node
        drawCircle(
            color = nodeColor.copy(alpha = 0.8f),
            radius = nodeSize,
            center = node
        )
    }
}

private fun DrawScope.drawHolographicCircles(
    rotation: Float,
    pulse: Float,
    primaryColor: Color,
    accentColor: Color
) {
    val centerX = size.width * 0.5f
    val centerY = size.height * 0.4f
    val center = Offset(centerX, centerY)

    rotate(rotation, pivot = center) {
        repeat(4) { ring ->
            val radius = (80f + ring * 40f) * (1f + sin(pulse + ring) * 0.1f)
            val ringColor = if (ring % 2 == 0) primaryColor else accentColor
            val segments = 24 + ring * 8

            repeat(segments) { segment ->
                val angle = (segment * 360f / segments) * PI / 180f
                val segmentLength = 15f + sin(pulse + ring + segment * 0.3f) * 8f

                val x1 = centerX + cos(angle) * (radius - segmentLength / 2)
                val y1 = centerY + sin(angle) * (radius - segmentLength / 2)
                val x2 = centerX + cos(angle) * (radius + segmentLength / 2)
                val y2 = centerY + sin(angle) * (radius + segmentLength / 2)

                val alpha = 0.4f + 0.3f * sin(pulse + segment * 0.2f)

                drawLine(
                    color = ringColor.copy(alpha = alpha),
                    start = Offset(x1.toFloat(), y1.toFloat()),
                    end = Offset(x2.toFloat(), y2.toFloat()),
                    strokeWidth = (2f + ring * 0.5f).dp.toPx()
                )
            }
        }
    }
}

private fun DrawScope.drawAIWaveSpectrum(
    waveOffset: Float,
    pulse: Float,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    val barCount = 32
    val barWidth = size.width / barCount
    val maxHeight = size.height * 0.35f

    val colors = listOf(primaryColor, secondaryColor, accentColor)

    repeat(barCount) { i ->
        val x = i * barWidth + barWidth * 0.5f
        val frequency1 = 0.2f + i * 0.05f
        val frequency2 = 0.1f + i * 0.03f

        // Create complex wave pattern
        val wave1 = sin(waveOffset + i * frequency1) * 0.5f + 0.5f
        val wave2 = sin(pulse + i * frequency2) * 0.3f + 0.3f
        val combinedWave = (wave1 + wave2) * 0.5f

        val height = maxHeight * (0.1f + 0.9f * combinedWave)
        val colorIndex = i % colors.size
        val color = colors[colorIndex]

        val startY = size.height * 0.85f

        // Main spectrum bar
        drawRect(
            color = color.copy(alpha = 0.8f),
            topLeft = Offset(x - barWidth * 0.35f, startY - height),
            size = androidx.compose.ui.geometry.Size(barWidth * 0.7f, height)
        )

        // AI processing indicator (small dots above bars)
        if (combinedWave > 0.7f) {
            repeat(3) { dot ->
                drawCircle(
                    color = color.copy(alpha = 0.6f),
                    radius = 2f,
                    center = Offset(x, startY - height - (dot + 1) * 15f)
                )
            }
        }
    }
}

private fun DrawScope.drawFloatingDataNodes(
    stream: Float,
    pulse: Float,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    val colors = listOf(primaryColor, secondaryColor, accentColor)

    repeat(15) { i ->
        val baseX = (i * 60f + stream * 100f) % (size.width + 100f) - 50f
        val baseY = size.height * (0.2f + (i % 3) * 0.25f)

        val floatY = baseY + sin(pulse + i * 1.2f) * 30f
        val position = Offset(baseX, floatY)

        val nodeSize = 6f + sin(pulse + i * 0.5f) * 3f
        val color = colors[i % colors.size]

        // Data trail effect
        repeat(3) { trail ->
            val trailX = baseX - trail * 20f
            val trailAlpha = (0.4f - trail * 0.1f) * (0.5f + sin(pulse + i) * 0.5f)

            if (trailX > -50f) {
                drawCircle(
                    color = color.copy(alpha = trailAlpha),
                    radius = nodeSize * (1f - trail * 0.2f),
                    center = Offset(trailX, floatY)
                )
            }
        }

        // Main data node
        if (baseX > -50f && baseX < size.width + 50f) {
            drawCircle(
                color = color.copy(alpha = 0.8f),
                radius = nodeSize,
                center = position
            )

            // Inner core
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = nodeSize * 0.3f,
                center = position
            )
        }
    }
}

private fun DrawScope.drawEnergyField(
    pulse: Float,
    flow: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val fieldLines = 8

    repeat(fieldLines) { line ->
        val path = Path().apply {
            val startY = size.height * (0.1f + line * 0.1f)
            val amplitude = 40f + line * 10f
            val frequency = 0.01f + line * 0.003f
            val phase = flow * 2f * PI.toFloat() + line * 0.8f

            moveTo(0f, startY)

            var x = 0f
            while (x <= size.width) {
                val waveHeight = sin(x * frequency + phase) * amplitude *
                        (1f + sin(pulse + line * 0.5f) * 0.3f)
                val y = startY + waveHeight
                lineTo(x, y)
                x += 8f
            }
        }

        val color = if (line % 2 == 0) primaryColor else secondaryColor
        val alpha = 0.15f + 0.1f * sin(pulse + line * 0.3f)

        drawPath(
            path = path,
            color = color.copy(alpha = alpha),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = (1.5f + line * 0.3f).dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(10f, 5f)
                )
            )
        )
    }
}

private fun DrawScope.drawAIParticleSystem(
    stream: Float,
    pulse: Float,
    accentColor: Color
) {
    repeat(20) { i ->
        val angle = i * (360f / 20f) + stream * 180f
        val radius = 100f + sin(pulse + i * 0.4f) * 50f

        val centerX = size.width * 0.8f
        val centerY = size.height * 0.6f

        val x = centerX + cos(angle * PI / 180f) * radius
        val y = centerY + sin(angle * PI / 180f) * radius

        val particleSize = 3f + sin(pulse + i * 0.6f) * 2f
        val alpha = 0.4f + 0.3f * abs(sin(pulse + i * 0.2f))

        if (x >= 0 && x <= size.width && y >= 0 && y <= size.height) {
            // Particle glow
            drawCircle(
                color = accentColor.copy(alpha = alpha * 0.3f),
                radius = particleSize * 2f,
                center = Offset(x.toFloat(), y.toFloat())
            )

            // Main particle
            drawCircle(
                color = accentColor.copy(alpha = alpha),
                radius = particleSize,
                center = Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AIMusicPlayerBackgroundPreview() {
    MaterialTheme {
        AIMusicPlayerBackground(
            primaryColor = Color(0xFF6366F1),
            secondaryColor = Color(0xFF8B5CF6),
            accentColor = Color(0xFFEC4899)
        ) {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AIMusicPlayerBackgroundCustomPreview() {
    MaterialTheme {
        AIMusicPlayerBackground(
            primaryColor = Color(0xFF10B981),
            secondaryColor = Color(0xFF06B6D4),
            accentColor = Color(0xFFF59E0B)
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

