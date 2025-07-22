package com.faysal.zenify.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.ui.theme.Nunito

/**
 * RhythmTimeline - A modern, sleek timeline seekbar for music players
 *
 * Features:
 * - Smooth progress animation with spring physics
 * - Interactive dragging with haptic feedback
 * - Gradient progress track with glow effects
 * - Animated thumb with scale effects
 * - Responsive touch areas for better UX
 * - Elegant time display with remaining time countdown
 */


@Composable
fun RhythmTimeline(
    modifier: Modifier = Modifier,
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    progressGradient: Brush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF8B5CF6),
            Color(0xFFFFFFFF)
        )
    ),
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
    thumbColor: Color = MaterialTheme.colorScheme.onSurface,
    trackHeight: Float = 4f,
    thumbRadius: Float = 8f,
    glowEffect: Boolean = true
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    val trackHeightPx = with(density) { trackHeight.dp.toPx() }
    val thumbRadiusPx = with(density) { thumbRadius.dp.toPx() }
    val touchAreaPx = with(density) { 48.dp.toPx() }

    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val progress = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragProgress else progress.coerceIn(0f, 1f),
        animationSpec = tween(200),
        label = "RhythmTimelineProgress"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.2f else 1f,
        animationSpec = tween(150),
        label = "RhythmTimelineThumbScale"
    )

    val trackGlow by animateFloatAsState(
        targetValue = if (isDragging) 0.8f else 0.4f,
        animationSpec = tween(300),
        label = "RhythmTimelineGlow"
    )

    val currentMs = (animatedProgress * durationMs).toLong().coerceAtMost(durationMs)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(currentMs),
                fontFamily = Nunito,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .pointerInput(canvasWidth, durationMs) {
                        detectTapGestures { offset ->
                            val newProgress = (offset.x / canvasWidth).coerceIn(0f, 1f)
                            onSeek((newProgress * durationMs).toLong())
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
                    .pointerInput(canvasWidth, durationMs) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDragging = true
                                val initialProgress = (offset.x / canvasWidth).coerceIn(0f, 1f)
                                dragProgress = initialProgress
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDragEnd = {
                                isDragging = false
                                onSeek((dragProgress * durationMs).toLong())
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDragCancel = {
                                isDragging = false
                            },
                            onDrag = { change, _ ->
                                val pos = (change.position.x / canvasWidth).coerceIn(0f, 1f)
                                dragProgress = pos
                            }
                        )
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size -> canvasWidth = size.width.toFloat() }
                ) {
                    drawRhythmTimeline(
                        progress = animatedProgress,
                        progressGradient = progressGradient,
                        trackColor = trackColor,
                        thumbColor = thumbColor,
                        trackHeightPx = trackHeightPx,
                        thumbRadiusPx = thumbRadiusPx,
                        thumbScale = thumbScale,
                        glowAlpha = if (glowEffect) trackGlow else 0f,
                        isDragging = isDragging
                    )
                }
            }

            Text(
                text = formatTime(durationMs),
                fontFamily = Nunito,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun DrawScope.drawRhythmTimeline(
    progress: Float,
    progressGradient: Brush,
    trackColor: Color,
    thumbColor: Color,
    trackHeightPx: Float,
    thumbRadiusPx: Float,
    thumbScale: Float,
    glowAlpha: Float,
    isDragging: Boolean
) {
    val centerY = size.height / 2f
    val trackTop = centerY - trackHeightPx / 2f
    val trackBottom = centerY + trackHeightPx / 2f
    val progressWidth = progress * size.width
    val thumbX = progress * size.width
    val cornerRadius = trackHeightPx / 2f

    // Draw background track
    drawRoundRect(
        color = trackColor,
        topLeft = Offset(0f, trackTop),
        size = Size(size.width, trackHeightPx),
        cornerRadius = CornerRadius(cornerRadius)
    )

    // Draw progress track with gradient
    if (progressWidth > 0) {
        // Glow effect
        if (glowAlpha > 0f) {
            drawRoundRect(
                brush = progressGradient,
                topLeft = Offset(0f, trackTop - 1f),
                size = Size(progressWidth, trackHeightPx + 2f),
                cornerRadius = CornerRadius(cornerRadius),
                alpha = glowAlpha * 0.3f
            )
        }

        // Main progress track
        drawRoundRect(
            brush = progressGradient,
            topLeft = Offset(0f, trackTop),
            size = Size(progressWidth, trackHeightPx),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }

    // Draw thumb
    val scaledThumbRadius = thumbRadiusPx * thumbScale

    // Thumb glow
    if (glowAlpha > 0f) {
        drawCircle(
            color = thumbColor.copy(alpha = glowAlpha * 0.4f),
            radius = scaledThumbRadius * 1.5f,
            center = Offset(thumbX, centerY)
        )
    }

    // Thumb shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.2f),
        radius = scaledThumbRadius,
        center = Offset(thumbX + 1f, centerY + 1f)
    )

    // Main thumb
    drawCircle(
        color = thumbColor,
        radius = scaledThumbRadius,
        center = Offset(thumbX, centerY)
    )

    // Inner thumb highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = scaledThumbRadius * 0.4f,
        center = Offset(thumbX - scaledThumbRadius * 0.2f, centerY - scaledThumbRadius * 0.2f)
    )
}


@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun RhythmTimelinePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RhythmTimeline(
            modifier = Modifier.fillMaxWidth(),
            currentPositionMs = 45000,
            durationMs = 180000,
            onSeek = { }
        )
    }
}
