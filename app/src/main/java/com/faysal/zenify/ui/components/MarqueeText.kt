package com.faysal.zenify.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay


@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    speed: Float = 40f // pixels per second
) {
    val density = LocalDensity.current
    val textWidth = remember { mutableStateOf(0f) }
    val containerWidth = remember { mutableStateOf(0f) }
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(textWidth.value, containerWidth.value) {
        if (textWidth.value > containerWidth.value) {
            while (true) {
                offsetX.snapTo(0f)
                val distance = textWidth.value - containerWidth.value
                val duration = (distance / speed * 1000).toInt()
                offsetX.animateTo(
                    targetValue = -distance,
                    animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                )
                delay(1000)
            }
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                containerWidth.value = coordinates.size.width.toFloat()
            }
    ) {
        BasicText(
            text = text,
            modifier = Modifier
                .graphicsLayer(translationX = offsetX.value)
                .onGloballyPositioned {
                    textWidth.value = it.size.width.toFloat()
                },
            style = textStyle,
            maxLines = 1
        )
    }
}
