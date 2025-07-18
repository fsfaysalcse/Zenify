package com.faysal.zenify.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.faysal.zenify.R
import com.faysal.zenify.ui.theme.blackToDreamWave
import kotlinx.coroutines.launch
import me.fsfaysalcse.discoverbd.ui.model.DrawerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {

    val scope = rememberCoroutineScope()
    var drawerState by remember { mutableStateOf(DrawerState.CLOSED) }

    val drawerWidth = 700f

    val translationX = remember { Animatable(0f) }
    translationX.updateBounds(0f, drawerWidth)

    val draggableState = rememberDraggableState(onDelta = { dragAmount ->
        scope.launch {
            translationX.snapTo(translationX.value + dragAmount)
        }
    })

    val decay = rememberSplineBasedDecay<Float>()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DrawerScreen(
            modifier = Modifier,
            isDrawerOpen = drawerState != DrawerState.CLOSED
        )

        Box(
            modifier = Modifier.fillMaxSize()
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale = lerp(1f, 0.8f, translationX.value / drawerWidth)
                    this.scaleX = scale
                    this.scaleY = scale
                    shape = RoundedCornerShape(
                        size = if (DrawerState.OPEN == drawerState) 16.dp else 0.dp
                    )
                    clip = true
                }.draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity: Float ->
                        val decayX = decay.calculateTargetValue(
                            translationX.value,
                            velocity
                        )
                        scope.launch {
                            val targetX = if (decayX > drawerWidth * 0.5f) {
                                drawerWidth
                            } else {
                                0f
                            }

                            val canReachTargetWithDecay =
                                (decayX > targetX && targetX == drawerWidth || (decayX < targetX && targetX == 0f))

                            if (canReachTargetWithDecay) {
                                translationX.animateDecay(
                                    initialVelocity = velocity,
                                    animationSpec = decay
                                )
                            } else {
                                translationX.animateTo(
                                    targetValue = targetX,
                                    initialVelocity = velocity
                                )
                            }
                            drawerState = if (targetX == drawerWidth) {
                                DrawerState.OPEN
                            } else {
                                DrawerState.CLOSED
                            }
                        }

                    }
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(blackToDreamWave)
            ) {
                DashboardToolbar(drawerState = drawerState, onNavigationClick = {
                        scope.launch {
                            if (drawerState == DrawerState.OPEN) {
                                translationX.animateTo(0f)
                            } else {
                                translationX.animateTo(drawerWidth)
                            }
                            drawerState = if (drawerState == DrawerState.OPEN) {
                                DrawerState.CLOSED
                            } else {
                                DrawerState.OPEN
                            }
                        }
                })

                HomeScreen()
            }

        }
    }
}


@Composable
fun DashboardToolbar(
    drawerState: DrawerState, onNavigationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp)
            .wrapContentHeight().padding(horizontal = 16.dp)
            .padding(top = 10.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { onNavigationClick() }) {
            Crossfade(
                targetState = drawerState,
                animationSpec = tween(500, 0, easing = LinearOutSlowInEasing)
            ) { state ->
                val icon = if (state == DrawerState.CLOSED) {
                    R.drawable.ic_navigation
                } else {
                    R.drawable.ic_open_navigation
                }

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp, 40.dp),
                    tint = Color.White
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.bg_title_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(80.dp,40.dp).padding(start = 16.dp),
        )
    }
}


@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}
