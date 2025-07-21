package com.faysal.zenify.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import com.faysal.zenify.ui.components.MusicPlayerHomeBackground
import com.faysal.zenify.ui.components.NavigationDrawerBackground
import kotlinx.coroutines.launch
import me.fsfaysalcse.discoverbd.ui.model.DrawerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController? = null,
) {

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


        NavigationDrawerBackground() {
            DrawerScreen(
                modifier = Modifier,
                isDrawerOpen = drawerState != DrawerState.CLOSED
            )
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.translationX = translationX.value
                    val scale = lerp(1f, 0.8f, translationX.value / drawerWidth)
                    this.scaleX = scale
                    this.scaleY = scale
                    shape = RoundedCornerShape(
                        size = if (DrawerState.OPEN == drawerState) 16.dp else 0.dp
                    )
                    clip = true
                }
                .draggable(
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
            MusicPlayerHomeBackground() {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    HomeContent(
                        drawerState = drawerState,
                        onNavigationClick = {
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
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
