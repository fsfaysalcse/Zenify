package com.faysal.zenify.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.MusicPrimaryColor
import com.faysal.zenify.ui.theme.TabPrimary

@Composable
fun FloatingOrbTabBar(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTabIndex
            val orbScale by animateFloatAsState(
                targetValue = if (isSelected) pulseScale else 0.8f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                label = "orbScale"
            )
            val glowAlpha by animateFloatAsState(
                targetValue = if (isSelected) 0.6f else 0f,
                animationSpec = tween(500),
                label = "glowAlpha"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTabSelected(index) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .scale(orbScale)
                        .background(
                            brush = if (isSelected) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        TabPrimary.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    ),
                                    radius = 80f
                                )
                            } else Brush.radialGradient(listOf(Color.Transparent, Color.Transparent)),
                            shape = CircleShape
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(if (isSelected) 45.dp else 35.dp)
                            .background(
                                color = if (isSelected) TabPrimary else Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = title.first().toString(),
                            fontFamily = AvenirNext,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = if (isSelected) 16.sp else 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontFamily = AvenirNext,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) TabPrimary else Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun WaveformTabBar(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedTabIndex
                val barHeights = remember {
                    (0..7).map { (10..35).random().dp }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp)
                ) {
                    if (isSelected) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(40.dp)
                        ) {
                            repeat(8) { barIndex ->
                                val animatedHeight by animateFloatAsState(
                                    targetValue = if (isSelected) {
                                        (kotlin.math.sin(Math.toRadians((waveOffset + barIndex * 45).toDouble())).toFloat() * 0.5f + 1f)
                                    } else 0.3f,
                                    animationSpec = tween(200),
                                    label = "barHeight$barIndex"
                                )

                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(barHeights[barIndex] * animatedHeight)
                                        .background(
                                            color = TabPrimary.copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(1.5.dp)
                                        )
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title.first().toString(),
                                fontFamily = AvenirNext,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = title,
                        fontFamily = AvenirNext,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TabPrimary else Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RotatingVinylTabBar(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinylRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedTabIndex
                val angle = (index * 360f / tabs.size)
                val distance = 120f

                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.2f else 0.8f,
                    animationSpec = spring(dampingRatio = 0.6f),
                    label = "vinylScale$index"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = (distance * kotlin.math.cos(Math.toRadians(angle.toDouble()))).dp,
                            y = (distance * kotlin.math.sin(Math.toRadians(angle.toDouble()))).dp
                        )
                        .scale(animatedScale)
                        .clickable { onTabSelected(index) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 55.dp else 45.dp)
                            .rotate(if (isSelected) rotation else 0f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = if (isSelected) {
                                        listOf(
                                            TabPrimary,
                                            TabPrimary.copy(alpha = 0.7f),
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    } else {
                                        listOf(
                                            Color.White.copy(alpha = 0.1f),
                                            Color.White.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                    }
                                ),
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        repeat(3) { ringIndex ->
                            Box(
                                modifier = Modifier
                                    .size((15 + ringIndex * 8).dp)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected)
                                            Color.White.copy(alpha = 0.2f - ringIndex * 0.05f)
                                        else
                                            Color.White.copy(alpha = 0.05f),
                                        shape = CircleShape
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = TabPrimary.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = tabs.getOrNull(selectedTabIndex) ?: "",
            fontFamily = AvenirNext,
            fontWeight = FontWeight.Bold,
            color = TabPrimary,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ModernCustomTabBar(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTabIndex
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) TabPrimary else Color.White.copy(alpha = 0.6f),
                animationSpec = tween(300),
                label = "textColor"
            )
            val animatedScale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                animationSpec = spring(dampingRatio = 0.6f),
                label = "scale"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp)
                    .scale(animatedScale)
            ) {
                Text(
                    text = title,
                    fontFamily = AvenirNext,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = animatedColor,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(if (isSelected) 40.dp else 0.dp)
                        .background(
                            color = TabPrimary,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .graphicsLayer {
                            alpha = if (isSelected) 1f else 0f
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0a0a0a)
@Composable
fun ModernCustomTabBarPreview() {
    ModernCustomTabBar(
        tabs = listOf("Home", "Search", "Library", "Profile"),
        selectedTabIndex = 0,
        onTabSelected = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0a0a0a)
@Composable
fun FloatingOrbTabBarPreview() {
    FloatingOrbTabBar(
        tabs = listOf("Home", "Search", "Library", "Profile"),
        selectedTabIndex = 0,
        onTabSelected = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0a0a0a)
@Composable
fun WaveformTabBarPreview() {
    WaveformTabBar(
        tabs = listOf("Home", "Search", "Library", "Profile"),
        selectedTabIndex = 1,
        onTabSelected = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0a0a0a)
@Composable
fun RotatingVinylTabBarPreview() {
    RotatingVinylTabBar(
        tabs = listOf("Home", "Search", "Library", "Profile"),
        selectedTabIndex = 2,
        onTabSelected = {}
    )
}