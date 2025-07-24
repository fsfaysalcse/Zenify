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
import kotlin.math.sqrt

// ========== MAIN MUSIC PLAYER BACKGROUND ==========
@Composable
fun AIMusicPlayerBackground(
    primaryColor: Color = Color(0xFF6366F1),
    secondaryColor: Color = Color(0xFF8B5CF6),
    accentColor: Color = Color(0xFFEC4899),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "music_bg")

    val liquidFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "liquidFlow"
    )

    val crystalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "crystal"
    )

    val breathPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        primaryColor.copy(0.4f),
                        MaterialTheme.colorScheme.surface
                    ),
                    radius = 800f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLiquidWaves(liquidFlow, primaryColor, secondaryColor)
            drawCrystalFormations(crystalRotation, breathPulse, primaryColor, accentColor)
            drawFloatingBubbles(liquidFlow, breathPulse, secondaryColor, accentColor)
            drawEnergyRipples(breathPulse, liquidFlow, primaryColor)
        }
        content()
    }
}

// ========== FAVOURITE BACKGROUND ==========
@Composable
fun FavouriteBackground(
    primaryColor: Color = Color(0xFFFF6B6B),
    secondaryColor: Color = Color(0xFFFF8E8E),
    accentColor: Color = Color(0xFFFFB3B3),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fav_bg")

    val heartBeat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heartBeat"
    )

    val loveFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loveFlow"
    )

    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHeartParticles(heartBeat, loveFlow, primaryColor, secondaryColor)
            drawLoveAura(loveFlow, heartBeat, primaryColor, accentColor)
            drawSparkleEffect(sparkle, heartBeat, accentColor)
            drawFloatingHearts(loveFlow, primaryColor, secondaryColor)
        }
        content()
    }
}

// ========== EQUALIZER BACKGROUND ==========
@Composable
fun EqualizerBackground(
    primaryColor: Color = Color(0xFF00D4AA),
    secondaryColor: Color = Color(0xFF00B4D8),
    accentColor: Color = Color(0xFF0077B6),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq_bg")

    val waveSync by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveSync"
    )

    val frequencyPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frequency"
    )

    val mixerRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mixer"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF051A1A),
                        Color.Black
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFrequencyGrid(frequencyPulse, primaryColor, secondaryColor)
           // drawEQBands(waveSync, frequencyPulse, primaryColor, secondaryColor, accentColor)
            drawMixerDials(mixerRotate, waveSync, accentColor)
            drawSoundWaves(waveSync, primaryColor, secondaryColor)
        }
        content()
    }
}

// ========== SETTINGS BACKGROUND ==========
@Composable
fun SettingsBackground(
    primaryColor: Color = Color(0xFF9333EA),
    secondaryColor: Color = Color(0xFF7C3AED),
    accentColor: Color = Color(0xFF6366F1),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "settings_bg")

    val gearRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gears"
    )

    val circuitFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "circuit"
    )

    val configPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "config"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF0A0A1A),
                        Color.Black
                    ),
                    radius = 900f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircuitPattern(circuitFlow, primaryColor, secondaryColor)
            drawRotatingGears(gearRotation, configPulse, primaryColor, accentColor)
            drawConfigNodes(configPulse, circuitFlow, secondaryColor, accentColor)
            drawTechGrid(circuitFlow, primaryColor)
        }
        content()
    }
}

// ========== ABOUT BACKGROUND ==========
@Composable
fun AboutBackground(
    primaryColor: Color = Color(0xFFF59E0B),
    secondaryColor: Color = Color(0xFFEAB308),
    accentColor: Color = Color(0xFFFBBF24),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "about_bg")

    val storyFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "story"
    )

    val knowledgeOrbit by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "knowledge"
    )

    val wisdomPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wisdom"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF1A1000),
                        Color.Black
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawKnowledgeOrbs(knowledgeOrbit, wisdomPulse, primaryColor, secondaryColor)
            drawWisdomFlow(storyFlow, primaryColor, accentColor)
            drawInformationStream(storyFlow, knowledgeOrbit, secondaryColor, accentColor)
            drawBookPages(storyFlow, wisdomPulse, primaryColor)
        }
        content()
    }
}

// ========== MUSIC PLAYER DRAWING FUNCTIONS ==========
private fun DrawScope.drawLiquidWaves(flow: Float, primary: Color, secondary: Color) {
    repeat(5) { wave ->
        val path = Path().apply {
            val amplitude = 80f + wave * 20f
            val frequency = 0.008f + wave * 0.002f
            val phase = flow + wave * 1.2f

            moveTo(0f, size.height * 0.5f)

            var x = 0f
            while (x <= size.width) {
                val y1 = size.height * 0.5f + sin(x * frequency + phase) * amplitude
                val y2 = size.height * 0.5f + cos(x * frequency * 0.7f + phase) * amplitude * 0.6f
                val finalY = (y1 + y2) * 0.5f

                cubicTo(
                    x - 20f, finalY - 10f,
                    x + 20f, finalY + 10f,
                    x + 40f, finalY
                )
                x += 40f
            }
        }

        val color = if (wave % 2 == 0) primary else secondary
        drawPath(
            path = path,
            color = color.copy(alpha = 0.3f - wave * 0.05f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = (4f - wave * 0.5f).dp.toPx())
        )
    }
}

private fun DrawScope.drawCrystalFormations(rotation: Float, pulse: Float, primary: Color, accent: Color) {
    val formations = listOf(
        Offset(size.width * 0.3f, size.height * 0.2f),
        Offset(size.width * 0.7f, size.height * 0.3f),
        Offset(size.width * 0.2f, size.height * 0.7f),
        Offset(size.width * 0.8f, size.height * 0.8f)
    )

    formations.forEachIndexed { index, center ->
        val crystalSize = (30f + index * 10f) * pulse
        val sides = 6 + index
        val color = if (index % 2 == 0) primary else accent

        rotate(rotation + index * 45f, pivot = center) {
            val path = Path()
            repeat(sides) { side ->
                val angle = (side * 360f / sides) * PI / 180f
                val x = center.x + cos(angle) * crystalSize
                val y = center.y + sin(angle) * crystalSize

                if (side == 0) path.moveTo(x.toFloat(), y.toFloat())
                else path.lineTo(x.toFloat(), y.toFloat())
            }
            path.close()

            drawPath(path, color.copy(alpha = 0.4f))
            drawPath(
                path,
                color.copy(alpha = 0.8f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawFloatingBubbles(flow: Float, pulse: Float, secondary: Color, accent: Color) {
    repeat(12) { i ->
        val x = (size.width * 0.1f + (i % 4) * size.width * 0.25f + sin(flow + i) * 50f)
        val y = (size.height * 0.2f + (i % 3) * size.height * 0.3f + cos(flow + i * 1.5f) * 60f)
        val bubbleSize = (15f + i % 4 * 8f) * (0.8f + pulse * 0.2f)

        val color = if (i % 2 == 0) secondary else accent

        // Bubble glow
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = bubbleSize * 1.5f,
            center = Offset(x, y)
        )

        // Main bubble
        drawCircle(
            color = color.copy(alpha = 0.5f),
            radius = bubbleSize,
            center = Offset(x, y)
        )

        // Highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = bubbleSize * 0.3f,
            center = Offset(x - bubbleSize * 0.2f, y - bubbleSize * 0.2f)
        )
    }
}

private fun DrawScope.drawEnergyRipples(pulse: Float, flow: Float, primary: Color) {
    val center = Offset(size.width * 0.5f, size.height * 0.6f)

    repeat(4) { ring ->
        val radius = (100f + ring * 80f) * (0.8f + pulse * 0.4f)
        val alpha = (0.4f - ring * 0.08f) * abs(sin(flow + ring * 0.5f))

        drawCircle(
            color = primary.copy(alpha = alpha),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = (3f - ring * 0.5f).dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(15f, 10f)
                )
            )
        )
    }
}

// ========== FAVOURITE DRAWING FUNCTIONS ==========
private fun DrawScope.drawHeartParticles(beat: Float, flow: Float, primary: Color, secondary: Color) {
    repeat(8) { i ->
        val x = size.width * (0.2f + (i % 3) * 0.3f) + sin(flow + i) * 40f
        val y = size.height * (0.3f + (i % 2) * 0.4f) + cos(beat + i * 1.2f) * 30f
        val heartSize = 20f + sin(beat + i) * 8f

        val color = if (i % 2 == 0) primary else secondary
        drawHeart(Offset(x, y), heartSize, color.copy(alpha = 0.7f))
    }
}

private fun DrawScope.drawHeart(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        val x = center.x
        val y = center.y

        // Left curve
        moveTo(x, y + size * 0.3f)
        cubicTo(
            x - size * 0.5f, y - size * 0.1f,
            x - size * 0.5f, y - size * 0.7f,
            x, y - size * 0.3f
        )

        // Right curve
        cubicTo(
            x + size * 0.5f, y - size * 0.7f,
            x + size * 0.5f, y - size * 0.1f,
            x, y + size * 0.3f
        )

        // Bottom point
        lineTo(x, y + size * 0.7f)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawLoveAura(flow: Float, beat: Float, primary: Color, accent: Color) {
    val center = Offset(size.width * 0.5f, size.height * 0.4f)

    repeat(6) { ring ->
        val radius = (60f + ring * 40f) * (1f + sin(beat + ring * 0.3f) * 0.2f)
        val segments = 12 + ring * 4

        repeat(segments) { segment ->
            val angle = (segment * 360f / segments + flow * 60f) * PI / 180f
            val segmentSize = 8f + sin(beat + segment * 0.2f) * 4f

            val x = center.x + cos(angle) * radius
            val y = center.y + sin(angle) * radius

            val color = if (ring % 2 == 0) primary else accent
            drawCircle(
                color = color.copy(alpha = 0.4f - ring * 0.05f),
                radius = segmentSize,
                center = Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}

private fun DrawScope.drawSparkleEffect(sparkle: Float, beat: Float, accent: Color) {
    repeat(15) { i ->
        val angle = i * (360f / 15f) + sparkle
        val radius = 120f + sin(beat + i * 0.4f) * 60f

        val centerX = size.width * 0.7f
        val centerY = size.height * 0.3f

        val x = centerX + cos(angle * PI / 180f) * radius
        val y = centerY + sin(angle * PI / 180f) * radius

        val sparkleSize = 6f + sin(beat + i * 0.6f) * 4f
        val alpha = 0.6f + 0.4f * abs(sin(sparkle + i * 0.3f))

        // Cross sparkle
        drawLine(
            color = accent.copy(alpha = alpha),
            start = Offset(x.toFloat() - sparkleSize, y.toFloat()),
            end = Offset(x.toFloat() + sparkleSize, y.toFloat()),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = accent.copy(alpha = alpha),
            start = Offset(x.toFloat(), y.toFloat() - sparkleSize),
            end = Offset(x.toFloat(), y.toFloat() + sparkleSize),
            strokeWidth = 2.dp.toPx()
        )
    }
}

private fun DrawScope.drawFloatingHearts(flow: Float, primary: Color, secondary: Color) {
    repeat(6) { i ->
        val x = (i * 80f + flow * 150f) % (size.width + 100f) - 50f
        val y = size.height * (0.6f + (i % 2) * 0.2f) + sin(flow + i) * 20f
        val heartSize = 12f + i % 3 * 6f

        val color = if (i % 2 == 0) primary else secondary
        if (x > -50f && x < size.width + 50f) {
            drawHeart(Offset(x, y), heartSize, color.copy(alpha = 0.5f))
        }
    }
}

// ========== EQUALIZER DRAWING FUNCTIONS ==========
private fun DrawScope.drawFrequencyGrid(pulse: Float, primary: Color, secondary: Color) {
    val gridSpacing = 40f
    val color = primary.copy(alpha = 0.15f + 0.1f * sin(pulse))

    // Vertical lines
    var x = 0f
    while (x <= size.width) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx()
        )
        x += gridSpacing
    }

    // Horizontal lines
    var y = 0f
    while (y <= size.height) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
        )
        y += gridSpacing
    }
}

private fun DrawScope.drawEQBands(wave: Float, freq: Float, primary: Color, secondary: Color, accent: Color) {
    val bandCount = 10
    val bandWidth = size.width / bandCount
    val colors = listOf(primary, secondary, accent)

    repeat(bandCount) { i ->
        val x = i * bandWidth + bandWidth * 0.5f
        val frequency = 0.3f + i * 0.1f
        val height = size.height * 0.6f * (0.2f + 0.8f * abs(sin(wave + i * frequency)))

        val color = colors[i % colors.size]
        val startY = size.height * 0.8f

        // EQ Band
        drawRect(
            color = color.copy(alpha = 0.8f),
            topLeft = Offset(x - bandWidth * 0.3f, startY - height),
            size = androidx.compose.ui.geometry.Size(bandWidth * 0.6f, height)
        )

        // Frequency indicator
        val indicatorY = startY - height - 20f
        drawCircle(
            color = color,
            radius = 4f + sin(freq + i) * 2f,
            center = Offset(x, indicatorY)
        )
    }
}

private fun DrawScope.drawMixerDials(rotation: Float, wave: Float, accent: Color) {
    val dialPositions = listOf(
        Offset(size.width * 0.2f, size.height * 0.3f),
        Offset(size.width * 0.5f, size.height * 0.2f),
        Offset(size.width * 0.8f, size.height * 0.35f)
    )

    dialPositions.forEachIndexed { index, center ->
        val dialSize = 40f
        val dialRotation = rotation + index * 120f + sin(wave + index) * 30f

        // Dial background
        drawCircle(
            color = accent.copy(alpha = 0.3f),
            radius = dialSize,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )

        // Dial pointer
        rotate(dialRotation, pivot = center) {
            drawLine(
                color = accent,
                start = center,
                end = Offset(center.x, center.y - dialSize * 0.7f),
                strokeWidth = 4.dp.toPx()
            )
        }

        // Center dot
        drawCircle(
            color = accent,
            radius = 6f,
            center = center
        )
    }
}

private fun DrawScope.drawSoundWaves(wave: Float, primary: Color, secondary: Color) {
    repeat(3) { layer ->
        val path = Path().apply {
            val amplitude = 60f + layer * 20f
            val frequency = 0.015f + layer * 0.005f
            val yOffset = size.height * (0.6f + layer * 0.1f)

            moveTo(0f, yOffset)

            var x = 0f
            while (x <= size.width) {
                val y = yOffset + sin(x * frequency + wave + layer * PI.toFloat() / 2f) * amplitude
                lineTo(x, y)
                x += 5f
            }
        }

        val color = if (layer % 2 == 0) primary else secondary
        drawPath(
            path = path,
            color = color.copy(alpha = 0.6f - layer * 0.1f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = (3f + layer).dp.toPx())
        )
    }
}

// ========== SETTINGS DRAWING FUNCTIONS ==========
private fun DrawScope.drawCircuitPattern(flow: Float, primary: Color, secondary: Color) {
    val nodes = listOf(
        Offset(size.width * 0.1f, size.height * 0.2f),
        Offset(size.width * 0.4f, size.height * 0.3f),
        Offset(size.width * 0.7f, size.height * 0.1f),
        Offset(size.width * 0.3f, size.height * 0.6f),
        Offset(size.width * 0.8f, size.height * 0.5f),
        Offset(size.width * 0.6f, size.height * 0.8f)
    )

    // Circuit connections
    for (i in nodes.indices) {
        for (j in i + 1 until nodes.size) {
            val start = nodes[i]
            val end = nodes[j]
            val distance = sqrt((end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y))

            if (distance < 250f) {
                val progress = (flow + i * 0.2f) % 1f
                val connectionColor = if (i % 2 == 0) primary else secondary

                // Animated circuit line
                val animatedEnd = Offset(
                    start.x + (end.x - start.x) * progress,
                    start.y + (end.y - start.y) * progress
                )

                drawLine(
                    color = connectionColor.copy(alpha = 0.6f),
                    start = start,
                    end = animatedEnd,
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }

    // Circuit nodes
    nodes.forEachIndexed { index, node ->
        val nodeSize = 8f + sin(flow * 2f * PI.toFloat() + index) * 3f
        val nodeColor = if (index % 2 == 0) primary else secondary

        drawRect(
            color = nodeColor,
            topLeft = Offset(node.x - nodeSize, node.y - nodeSize),
            size = androidx.compose.ui.geometry.Size(nodeSize * 2, nodeSize * 2)
        )
    }
}

private fun DrawScope.drawRotatingGears(rotation: Float, pulse: Float, primary: Color, accent: Color) {
    val gearPositions = listOf(
        Offset(size.width * 0.3f, size.height * 0.4f),
        Offset(size.width * 0.7f, size.height * 0.6f)
    )

    gearPositions.forEachIndexed { index, center ->
        val gearSize = (50f + index * 20f) * pulse
        val teeth = 12 + index * 4
        val gearRotation = if (index % 2 == 0) rotation else -rotation
        val color = if (index % 2 == 0) primary else accent

        rotate(gearRotation, pivot = center) {
            // Gear teeth
            repeat(teeth) { tooth ->
                val angle = (tooth * 360f / teeth) * PI / 180f
                val innerRadius = gearSize * 0.7f
                val outerRadius = gearSize

                val innerX = center.x + cos(angle) * innerRadius
                val innerY = center.y + sin(angle) * innerRadius
                val outerX = center.x + cos(angle) * outerRadius
                val outerY = center.y + sin(angle) * outerRadius

                drawLine(
                    color = color.copy(alpha = 0.8f),
                    start = Offset(innerX.toFloat(), innerY.toFloat()),
                    end = Offset(outerX.toFloat(), outerY.toFloat()),
                    strokeWidth = 4.dp.toPx()
                )
            }

            // Gear center
            drawCircle(
                color = color,
                radius = gearSize * 0.3f,
                center = center
            )

            // Center hole
            drawCircle(
                color = Color.Black,
                radius = gearSize * 0.15f,
                center = center
            )
        }
    }
}

private fun DrawScope.drawConfigNodes(pulse: Float, flow: Float, secondary: Color, accent: Color) {
    val nodeGrid = listOf(
        Offset(size.width * 0.2f, size.height * 0.7f),
        Offset(size.width * 0.5f, size.height * 0.8f),
        Offset(size.width * 0.8f, size.height * 0.7f)
    )

    nodeGrid.forEachIndexed { index, node ->
        val nodeSize = (15f + index * 5f) * (0.8f + pulse * 0.4f)
        val alpha = 0.7f + 0.3f * sin(flow * 2f * PI.toFloat() + index * 1.2f)
        val color = if (index % 2 == 0) secondary else accent

        // Node glow
        drawCircle(
            color = color.copy(alpha = alpha * 0.3f),
            radius = nodeSize * 1.8f,
            center = node
        )

        // Main node
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = nodeSize,
            center = node
        )

        // Config indicator
        repeat(4) { indicator ->
            val indicatorAngle = (indicator * 90f + flow * 180f) * PI / 180f
            val indicatorRadius = nodeSize * 1.3f
            val indicatorX = node.x + cos(indicatorAngle) * indicatorRadius
            val indicatorY = node.y + sin(indicatorAngle) * indicatorRadius

            drawCircle(
                color = color.copy(alpha = 0.6f),
                radius = 3f,
                center = Offset(indicatorX.toFloat(), indicatorY.toFloat())
            )
        }
    }
}

private fun DrawScope.drawTechGrid(flow: Float, primary: Color) {
    val gridSize = 80f
    val alpha = 0.1f + 0.05f * sin(flow * 2f * PI.toFloat())

    // Animated tech grid
    var x = (flow * 50f) % gridSize
    while (x <= size.width) {
        drawLine(
            color = primary.copy(alpha = alpha),
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(10f, 20f)
            )
        )
        x += gridSize
    }

    var y = (flow * 30f) % gridSize
    while (y <= size.height) {
        drawLine(
            color = primary.copy(alpha = alpha),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(15f, 25f)
            )
        )
        y += gridSize
    }
}

// ========== ABOUT DRAWING FUNCTIONS ==========
private fun DrawScope.drawKnowledgeOrbs(orbit: Float, pulse: Float, primary: Color, secondary: Color) {
    val centerX = size.width * 0.5f
    val centerY = size.height * 0.4f
    val center = Offset(centerX, centerY)

    repeat(5) { orb ->
        val angle = (orb * 72f + orbit) * PI / 180f
        val radius = (120f + orb * 30f) * pulse
        val orbSize = 25f + orb * 8f

        val x = centerX + cos(angle) * radius
        val y = centerY + sin(angle) * radius
        val position = Offset(x.toFloat(), y.toFloat())

        val color = if (orb % 2 == 0) primary else secondary

        // Knowledge aura
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = orbSize * 2f,
            center = position
        )

        // Main orb
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = orbSize,
            center = position
        )

        // Inner wisdom
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = orbSize * 0.4f,
            center = position
        )

        // Connection to center
        drawLine(
            color = color.copy(alpha = 0.3f),
            start = center,
            end = position,
            strokeWidth = 1.5f.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(8f, 12f)
            )
        )
    }
}

private fun DrawScope.drawWisdomFlow(story: Float, primary: Color, accent: Color) {
    repeat(4) { stream ->
        val path = Path().apply {
            val amplitude = 100f + stream * 30f
            val frequency = 0.006f + stream * 0.002f
            val yOffset = size.height * (0.6f + stream * 0.1f)
            val phase = story + stream * 1.5f

            moveTo(0f, yOffset)

            var x = 0f
            while (x <= size.width) {
                val wave1 = sin(x * frequency + phase) * amplitude
                val wave2 = cos(x * frequency * 1.3f + phase) * amplitude * 0.6f
                val y = yOffset + (wave1 + wave2) * 0.5f

                if (x == 0f) moveTo(x, y) else lineTo(x, y)
                x += 10f
            }
        }

        val color = if (stream % 2 == 0) primary else accent
        drawPath(
            path = path,
            color = color.copy(alpha = 0.4f - stream * 0.08f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = (3f + stream * 0.5f).dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(20f, 15f)
                )
            )
        )
    }
}

private fun DrawScope.drawInformationStream(story: Float, orbit: Float, secondary: Color, accent: Color) {
    repeat(18) { particle ->
        val streamPath = particle / 18f
        val x = streamPath * size.width + sin(story + particle * 0.5f) * 40f
        val y = size.height * 0.8f + cos(orbit + particle * 0.3f) * 60f

        val particleSize = 4f + particle % 3 * 3f
        val alpha = 0.6f + 0.4f * abs(sin(story + particle * 0.4f))
        val color = if (particle % 2 == 0) secondary else accent

        // Information particle
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = particleSize,
            center = Offset(x, y)
        )

        // Data trail
        repeat(3) { trail ->
            val trailX = x - trail * 25f
            val trailAlpha = alpha * (0.5f - trail * 0.15f)

            if (trailX > 0) {
                drawCircle(
                    color = color.copy(alpha = trailAlpha),
                    radius = particleSize * (1f - trail * 0.2f),
                    center = Offset(trailX, y)
                )
            }
        }
    }
}

private fun DrawScope.drawBookPages(story: Float, pulse: Float, primary: Color) {
    val bookPositions = listOf(
        Offset(size.width * 0.15f, size.height * 0.3f),
        Offset(size.width * 0.85f, size.height * 0.5f)
    )

    bookPositions.forEachIndexed { index, position ->
        val pageWidth = 40f * pulse
        val pageHeight = 60f * pulse
        val pageOffset = sin(story + index * 2f) * 8f

        // Book pages (multiple layers)
        repeat(5) { page ->
            val pageX = position.x + page * 3f + pageOffset
            val pageY = position.y + page * 2f
            val pageAlpha = 0.6f - page * 0.1f

            drawRect(
                color = primary.copy(alpha = pageAlpha),
                topLeft = Offset(pageX, pageY),
                size = androidx.compose.ui.geometry.Size(pageWidth, pageHeight),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f.dp.toPx())
            )

            // Page lines
            repeat(4) { line ->
                val lineY = pageY + (line + 1) * pageHeight / 5f
                drawLine(
                    color = primary.copy(alpha = pageAlpha * 0.5f),
                    start = Offset(pageX + 5f, lineY),
                    end = Offset(pageX + pageWidth - 5f, lineY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

// ========== PREVIEW COMPOSABLES ==========
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
private fun FavouriteBackgroundPreview() {
    MaterialTheme {
        FavouriteBackground(
            primaryColor = Color(0xFFFF6B6B),
            secondaryColor = Color(0xFFFF8E8E),
            accentColor = Color(0xFFFFB3B3)
        ) {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EqualizerBackgroundPreview() {
    MaterialTheme {
        EqualizerBackground(
            primaryColor = Color(0xFF00D4AA),
            secondaryColor = Color(0xFF00B4D8),
            accentColor = Color(0xFF0077B6)
        ) {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsBackgroundPreview() {
    MaterialTheme {
        SettingsBackground(
            primaryColor = Color(0xFF9333EA),
            secondaryColor = Color(0xFF7C3AED),
            accentColor = Color(0xFF6366F1)
        ) {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutBackgroundPreview() {
    MaterialTheme {
        AboutBackground(
            primaryColor = Color(0xFFF59E0B),
            secondaryColor = Color(0xFFEAB308),
            accentColor = Color(0xFFFBBF24)
        ) {
            Box(modifier = Modifier.size(400.dp))
        }
    }
}