package com.faysal.zenify.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R
import com.faysal.zenify.ui.theme.AvenirNext
import me.fsfaysalcse.discoverbd.ui.model.DrawerState

@Composable
fun ModernSearchBar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = DrawerState.CLOSED,
    onNavigationClick: () -> Unit = {},
    onSearchTextChange: (String) -> Unit = {},
    placeholder: String = "Search...",
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
    elevation: Int = 4
) {
    var searchText by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Icon (Left side) with Animation
            Crossfade(
                targetState = drawerState,
                animationSpec = tween(300, 0, easing = LinearOutSlowInEasing),
                label = "drawer_animation"
            ) { state ->
                val icon = when (state) {
                    DrawerState.CLOSED -> R.drawable.ic_navigation
                    else -> R.drawable.ic_open_navigation
                }

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onNavigationClick() },
                    color = Color.Transparent
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "Navigation",
                            tint = contentColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Search TextField (Center - takes remaining space)
            BasicTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                    onSearchTextChange(newText)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = AvenirNext,
                    fontSize = 16.sp,
                    color = contentColor,
                    fontWeight = FontWeight.Normal
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontFamily = AvenirNext,
                                fontSize = 16.sp,
                                color = contentColor.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Search Icon (Right side)
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Alternative gradient version
@Composable
fun GradientSearchBar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = DrawerState.CLOSED,
    onNavigationClick: () -> Unit = {},
    onSearchTextChange: (String) -> Unit = {},
    placeholder: String = "Search...",
    gradientColors: List<Color> = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2)
    )
) {
    var searchText by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(gradientColors)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Icon (Left side) with Animation
            Crossfade(
                targetState = drawerState,
                animationSpec = tween(300, 0, easing = LinearOutSlowInEasing),
                label = "drawer_animation"
            ) { state ->
                val icon = when (state) {
                    DrawerState.CLOSED -> R.drawable.ic_navigation
                    else -> R.drawable.ic_open_navigation
                }

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onNavigationClick() },
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "Navigation",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Search TextField (Center)
            BasicTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                    onSearchTextChange(newText)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = AvenirNext,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontFamily = AvenirNext,
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Search Icon (Right side)
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewModernSearchBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Light theme version
        ModernSearchBar(
            drawerState = DrawerState.CLOSED,
            onNavigationClick = {},
            onSearchTextChange = {},
            placeholder = "Search for anything..."
        )

        // Dark theme version
        ModernSearchBar(
            drawerState = DrawerState.OPEN,
            onNavigationClick = {},
            onSearchTextChange = {},
            placeholder = "Search for anything...",
            backgroundColor = Color(0xFF2D2D2D),
            contentColor = Color.White
        )

        // Gradient version
        GradientSearchBar(
            drawerState = DrawerState.CLOSED,
            onNavigationClick = {},
            onSearchTextChange = {},
            placeholder = "Search with style..."
        )
    }
}