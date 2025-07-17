/**
 * Enhanced GestureMusicButton with improved gesture detection and play/pause functionality
 *
 * Features:
 * - Reliable play/pause tap detection
 * - Smooth drag gestures (left/right for track control, up/down for volume)
 * - Hold gesture support with proper timing
 * - Haptic feedback for better user experience
 * - Animated visual feedback with scaling and icon transitions
 */

package com.faysal.zenify.ui.components

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.faysal.zenify.R
import com.faysal.zenify.ui.states.GestureAction
import com.faysal.zenify.ui.theme.TrackGradient
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureMusicButton(
    modifier: Modifier = Modifier,
    coverPrimary: Color,
    isPlaying: Boolean,
    onPlayPause: (Boolean) -> Unit,
    onLongPress: () -> Unit,
    onSwipe: (GestureAction) -> Unit,
    onHold: (GestureAction.Hold) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var hasDraggedSignificantly by remember { mutableStateOf(false) }
    var currentGesture by remember { mutableStateOf<GestureAction?>(null) }
    var gestureConfirmed by remember { mutableStateOf(false) }
    var holdTriggered by remember { mutableStateOf(false) }
    var gestureIcon by remember { mutableStateOf<Int?>(null) }
    var dragStartTime by remember { mutableLongStateOf(0L) }

    val maxHorizontalDragPx = with(density) { (100.dp).toPx() }
    val maxVerticalDragPx = with(density) { (60.dp).toPx() }
    val horizontalThreshold = maxHorizontalDragPx * 0.35f
    val verticalThreshold = maxVerticalDragPx * 0.6f
    val minimumMovementForGesture = with(density) { 12.dp.toPx() }
    val tapMovementThreshold = with(density) { 6.dp.toPx() }

    val dragDistance by remember {
        derivedStateOf {
            val absX = abs(offsetX)
            val absY = abs(offsetY)
            if (absX > absY) absX else absY
        }
    }

    val dragIntensity by remember {
        derivedStateOf {
            val maxDrag =
                if (abs(offsetX) > abs(offsetY)) maxHorizontalDragPx else maxVerticalDragPx
            (dragDistance / maxDrag).coerceIn(0f, 1f)
        }
    }

    val gestureIconAlpha by animateFloatAsState(
        targetValue = if (gestureIcon != null && isDragging) 1f else 0f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "GestureIconAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isDragging) 1f + (dragIntensity * 0.08f) else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ButtonScale"
    )

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = if (isDragging) spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "AnimatedOffsetX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = if (isDragging) spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "AnimatedOffsetY"
    )

    val knobBackgroundAlpha by animateFloatAsState(
        targetValue = if (gestureIconAlpha == 0f) 0.5f else 0.8f + (dragIntensity * 0.2f),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "KnobBackgroundAlpha"
    )

    LaunchedEffect(currentGesture, gestureConfirmed, isDragging) {
        if (currentGesture != null && gestureConfirmed && !holdTriggered && isDragging) {
            delay(600)
            if (currentGesture != null && !holdTriggered && isDragging) {
                holdTriggered = true
                val holdAction = when (currentGesture) {
                    GestureAction.Left -> GestureAction.Hold.Left
                    GestureAction.Right -> GestureAction.Hold.Right
                    GestureAction.Up -> GestureAction.Hold.Up
                    GestureAction.Down -> GestureAction.Hold.Down
                    else -> null
                }
                holdAction?.let {
                    onHold(it)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        }
    }

    Box(
        modifier = modifier.size(80.dp, 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset {
                    val clampedX =
                        animatedOffsetX.coerceIn(-maxHorizontalDragPx, maxHorizontalDragPx)
                    val clampedY = animatedOffsetY.coerceIn(-maxVerticalDragPx, maxVerticalDragPx)
                    IntOffset(clampedX.roundToInt(), clampedY.roundToInt())
                }
                .size(80.dp)
                .scale(buttonScale)
                .clip(CircleShape)
                .background(
                    shape = CircleShape,
                    color = coverPrimary.copy(alpha = 0.3f)
                )
                .padding(10.dp)
                .background(
                    shape = CircleShape,
                    color = coverPrimary
                )
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            hasDraggedSignificantly = false
                            dragStartTime = System.currentTimeMillis()
                        },
                        onDragEnd = {
                            val dragDuration = System.currentTimeMillis() - dragStartTime

                            if (gestureConfirmed && !holdTriggered && currentGesture != null && hasDraggedSignificantly) {
                                onSwipe(currentGesture!!)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }

                            isDragging = false
                            hasDraggedSignificantly = false
                            offsetX = 0f
                            offsetY = 0f
                            currentGesture = null
                            gestureConfirmed = false
                            holdTriggered = false
                            gestureIcon = null
                        },
                        onDrag = { _, dragAmount ->
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y

                            val totalDragDistance =
                                kotlin.math.sqrt(offsetX * offsetX + offsetY * offsetY)

                            if (totalDragDistance > tapMovementThreshold) {
                                hasDraggedSignificantly = true
                            }

                            val absX = abs(offsetX)
                            val absY = abs(offsetY)

                            if (absX > absY) {
                                offsetY = 0f
                            } else {
                                offsetX = 0f
                            }

                            val finalAbsX = abs(offsetX)
                            val finalAbsY = abs(offsetY)

                            if (finalAbsX > minimumMovementForGesture || finalAbsY > minimumMovementForGesture) {
                                val newGesture = when {
                                    finalAbsX > finalAbsY && offsetX > horizontalThreshold -> GestureAction.Right
                                    finalAbsX > finalAbsY && offsetX < -horizontalThreshold -> GestureAction.Left
                                    finalAbsY > finalAbsX && offsetY < -verticalThreshold -> GestureAction.Up
                                    finalAbsY > finalAbsX && offsetY > verticalThreshold -> GestureAction.Down
                                    else -> null
                                }

                                if (newGesture != null && newGesture != currentGesture) {
                                    currentGesture = newGesture
                                    gestureConfirmed = true
                                    holdTriggered = false

                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                                    gestureIcon = when (newGesture) {
                                        GestureAction.Left -> R.drawable.ic_previous
                                        GestureAction.Right -> R.drawable.ic_next
                                        GestureAction.Up -> R.drawable.ic_volume_up
                                        GestureAction.Down -> R.drawable.ic_volume_down
                                        else -> null
                                    }
                                }
                            }
                        }
                    )
                }
                .combinedClickable(
                    onClick = {
                        if (!hasDraggedSignificantly) {
                            onPlayPause(!isPlaying)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    onLongClick = {
                        if (!hasDraggedSignificantly) {
                            onLongPress()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val iconPainter = gestureIcon?.let {
                painterResource(id = it)
            } ?: if (isPlaying) {
                painterResource(id = R.drawable.ic_pause)
            } else {
                painterResource(id = R.drawable.ic_play)
            }

            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = Color.White.copy(
                    alpha = if (gestureIcon != null) gestureIconAlpha else 1f
                ),
                modifier = Modifier
                    .size((48 + (dragIntensity * 8)).dp)
                    .graphicsLayer {
                        alpha = if (gestureIcon != null) gestureIconAlpha else 1f
                        scaleX = 1f + (dragIntensity * 0.08f)
                        scaleY = 1f + (dragIntensity * 0.08f)
                    }
            )
        }
    }
}

@Preview
@Composable
fun GestureMusicButtonPreview() {
    var isPlaying by remember { mutableStateOf(false) }

    GestureMusicButton(
        isPlaying = isPlaying,
        onPlayPause = {
            isPlaying = it
            Log.d("GestureMusicButton", "Play/Pause toggled: $it")
        },
        coverPrimary = Color.Black,
        onLongPress = {
            Log.d("GestureMusicButton", "Long press detected")
        },
        onSwipe = { gesture ->
            Log.d("GestureMusicButton", "Swipe detected: ${gesture::class.simpleName}")
        },
        onHold = { holdGesture ->
            Log.d("GestureMusicButton", "Hold detected: ${holdGesture::class.simpleName}")
        }
    )
}