package com.faysal.zenify.ui.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.faysal.zenify.R
import com.faysal.zenify.ui.theme.MusicPrimaryColor
import com.faysal.zenify.ui.theme.MusicSecondaryColor
import com.faysal.zenify.ui.theme.TrackGradient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * GestureMusicButton is a circular interactive media control widget that:
 *
 * - Toggles play/pause on tap
 * - Detects directional drags in 4 strict axes (Up, Down, Left, Right)
 * - Supports both single swipe and swipe+hold behaviors
 * - Supports press-and-hold (long press)
 *
 * It executes gesture-based actions via callback functions provided by the caller.
 *
 * @param modifier Modifier to be applied to the widget
 * @param isPlaying Initial playing state
 * @param onPlayPause Toggle callback for play/pause
 * @param onLongPress Callback for long press (press & hold without drag)
 * @param onSwipe Callback for directional swipe (called on release)
 * @param onHold Callback for swipe-and-hold after 500ms
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureMusicButton(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onPlayPause: (Boolean) -> Unit,
    onLongPress: () -> Unit,
    onSwipe: (String) -> Unit,
    onHold: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var swipeDirection by remember { mutableStateOf<String?>(null) }
    var actionLogged by remember { mutableStateOf(false) }
    var gestureIcon by remember { mutableStateOf<Int?>(null) }

    val gestureIconAlpha by animateFloatAsState(
        targetValue = if (gestureIcon != null) 1f else 0f,
        animationSpec = tween(300),
        label = "GestureIconAlpha"
    )

    val coroutineScope = rememberCoroutineScope()
    var holdJob by remember { mutableStateOf<Job?>(null) }

    val circleRadiusDp = 240.dp
    val buttonSizeDp = 100.dp
    val maxDragRadiusPx = with(LocalDensity.current) {
        ((circleRadiusDp - buttonSizeDp / 2) * 0.6f).toPx()
    }

    val animatedBrush by animateColorAsState(
        targetValue = if (isPlaying) MusicPrimaryColor else MusicSecondaryColor,
        animationSpec = tween(600),
        label = "GradientColor"
    )

    val knobBackgroundAlpha by animateFloatAsState(
        targetValue = if (gestureIconAlpha == 0f) 0.5f else 1f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = "KnobBackgroundAlpha"
    )


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(circleRadiusDp)
                .border(
                    width = 3.dp,
                    brush = TrackGradient,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .offset {
                    val (x, y) = limitOffsetToAxis(offsetX, offsetY, maxDragRadiusPx)
                    IntOffset(x.roundToInt(), y.roundToInt())
                }
                .size(buttonSizeDp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1a1a2e),
                            Color(0xFF16213e),
                            animatedBrush
                        ),
                    ),
                    alpha = knobBackgroundAlpha
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            holdJob?.cancel()
                            if (!actionLogged && swipeDirection != null) {
                                onSwipe(swipeDirection!!)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            offsetX = 0f
                            offsetY = 0f
                            swipeDirection = null
                            actionLogged = false
                            gestureIcon = null
                        },
                        onDrag = { _, dragAmount ->
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y

                            val absX = abs(offsetX)
                            val absY = abs(offsetY)

                            val direction = when {
                                absX > absY && offsetX > 0 -> "Right"
                                absX > absY && offsetX < 0 -> "Left"
                                absY > absX && offsetY < 0 -> "Up"
                                absY > absX && offsetY > 0 -> "Down"
                                else -> null
                            }

                            direction?.let {
                                val distance = if (it == "Left" || it == "Right") absX else absY

                                if (distance > maxDragRadiusPx * 0.6f && swipeDirection != it) {
                                    swipeDirection = it
                                    actionLogged = false

                                    gestureIcon = when (it) {
                                        "Left" -> R.drawable.ic_previous
                                        "Right" -> R.drawable.ic_next
                                        "Up" -> R.drawable.ic_volume_up
                                        "Down" -> R.drawable.ic_volume_down
                                        else -> null
                                    }

                                    holdJob?.cancel()
                                    holdJob = coroutineScope.launch {
                                        delay(500)
                                        if (!actionLogged) {
                                            onHold(it)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            actionLogged = true
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                .combinedClickable(
                    onClick = {
                        onPlayPause(!isPlaying)
                    },
                    onLongClick = {
                        onLongPress()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val gestureIconPainter: Painter? = gestureIcon?.let {
                painterResource(id = it)
            } ?: if (isPlaying) {
                painterResource(id = R.drawable.ic_pause)
            } else {
                painterResource(id = R.drawable.ic_play)
            }

            if (gestureIconPainter != null) {
                Icon(
                    painter = gestureIconPainter,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = if (gestureIcon != null) gestureIconAlpha else 1f),
                    modifier = Modifier
                        .size(70.dp)
                        .graphicsLayer {
                            alpha = if (gestureIcon != null) gestureIconAlpha else 1f
                        }
                )
            }
        }
    }
}

private fun limitOffsetToAxis(x: Float, y: Float, maxRadius: Float): Pair<Float, Float> {
    return if (abs(x) > abs(y)) {
        val clampedX = x.coerceIn(-maxRadius, maxRadius)
        clampedX to 0f
    } else {
        val clampedY = y.coerceIn(-maxRadius, maxRadius)
        0f to clampedY
    }
}


@Preview
@Composable
fun GestureMusicButtonPreview() {
    var isPlaying by remember { mutableStateOf(false) }

    GestureMusicButton(
        isPlaying = isPlaying,
        onPlayPause = { isPlaying = it },
        onLongPress = { /* Handle long press */ },
        onSwipe = { direction -> println("Swiped: $direction") },
        onHold = { direction -> println("Held: $direction") }
    )
}