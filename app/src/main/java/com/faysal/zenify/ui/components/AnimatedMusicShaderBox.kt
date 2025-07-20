package com.faysal.zenify.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.Language
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FallbackMusicBox(
    modifier: Modifier = Modifier,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FallbackAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        cos(rotation * PI.toFloat() / 180) * 0.3f + 0.5f,
                        sin(rotation * PI.toFloat() / 180) * 0.3f + 0.5f
                    )
                )
            )
    )
}

@Composable
fun FallbackAnimatedMusicBox(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    secondaryColor: Color,
    animationDuration: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FallbackColorAnimation")

    val colorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ColorTransition"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val currentColor = Color(
        red = primaryColor.red + (secondaryColor.red - primaryColor.red) * colorProgress,
        green = primaryColor.green + (secondaryColor.green - primaryColor.green) * colorProgress,
        blue = primaryColor.blue + (secondaryColor.blue - primaryColor.blue) * colorProgress,
        alpha = 1f
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        currentColor.copy(alpha = 0.9f),
                        currentColor.copy(alpha = 0.5f),
                        primaryColor.copy(alpha = 0.3f),
                        secondaryColor.copy(alpha = 0.5f),
                        currentColor.copy(alpha = 0.9f)
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        cos(rotation * PI.toFloat() / 180) * 0.2f + 0.5f,
                        sin(rotation * PI.toFloat() / 180) * 0.2f + 0.5f
                    )
                )
            )
    )
}

@Language("AGSL")
val MUSIC_SHADER = """
    uniform float iTime;
    uniform float2 iResolution;
    uniform float4 iColor;
    
    float f(float3 p) {
        p.z -= iTime * 10.0;
        float a = p.z * 0.1;
        float2x2 rot = float2x2(cos(a), sin(a), -sin(a), cos(a));
        p.xy = rot * p.xy;
        return 0.1 - length(cos(p.xy) + sin(p.yz));
    }
    
    half4 main(float2 fragCoord) {
        float3 d = float3(0.5 - fragCoord / iResolution.y, 1.0);
        float3 p = float3(0.0);
        for (int i = 0; i < 32; i++) {
            p += f(p) * d;
        }
        float3 col = (sin(p) + iColor.rgb) / length(p);
        return half4(col, 1.0);
    }
""".trimIndent()

@Composable
fun MusicShaderBox(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6366F1)
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val infiniteTransition = rememberInfiniteTransition(label = "ShaderAnimation")
        val time by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000),
                repeatMode = RepeatMode.Restart
            ),
            label = "ShaderTime"
        )

        val shader = remember { RuntimeShader(MUSIC_SHADER) }

        Box(
            modifier = modifier
                .drawWithCache {
                    val width = size.width
                    val height = size.height

                    shader.setFloatUniform("iTime", time)
                    shader.setFloatUniform("iResolution", width, height)
                    shader.setColorUniform("iColor", color.toArgb())

                    val shaderBrush = ShaderBrush(shader)

                    onDrawBehind {
                        drawRect(shaderBrush)
                    }
                }
        )
    } else {
        FallbackMusicBox(
            modifier = modifier,
            color = color
        )
    }
}

@Composable
fun AnimatedMusicShaderBox(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF6366F1),
    secondaryColor: Color = Color(0xFF8B5CF6),
    animationDuration: Int = 15000
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val infiniteTransition = rememberInfiniteTransition(label = "ColorAnimation")

        val animatedTime by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000),
                repeatMode = RepeatMode.Restart
            ),
            label = "ShaderTime"
        )

        val colorProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(animationDuration),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ColorTransition"
        )

        val currentColor = Color(
            red = primaryColor.red + (secondaryColor.red - primaryColor.red) * colorProgress,
            green = primaryColor.green + (secondaryColor.green - primaryColor.green) * colorProgress,
            blue = primaryColor.blue + (secondaryColor.blue - primaryColor.blue) * colorProgress,
            alpha = 1f
        )

        val shader = remember { RuntimeShader(MUSIC_SHADER) }

        Box(
            modifier = modifier
                .drawWithCache {
                    val width = size.width
                    val height = size.height

                    shader.setFloatUniform("iTime", animatedTime)
                    shader.setFloatUniform("iResolution", width, height)
                    shader.setColorUniform("iColor", currentColor.toArgb())

                    val shaderBrush = ShaderBrush(shader)

                    onDrawBehind {
                        drawRect(shaderBrush)
                    }
                }
        )
    } else {
        FallbackAnimatedMusicBox(
            modifier = modifier,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            animationDuration = animationDuration
        )
    }
}

@Preview
@Composable
fun MusicShaderBoxPreview() {
    Box(modifier = Modifier.size(200.dp)) {
        MusicShaderBox(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF6366F1)
        )
    }
}

@Preview
@Composable
fun AnimatedMusicShaderBoxPreview() {
    Box(modifier = Modifier.size(200.dp)) {
        AnimatedMusicShaderBox(
            modifier = Modifier.fillMaxSize(),
            primaryColor = Color(0xFF6366F1),
            secondaryColor = Color(0xFFEC4899)
        )
    }
}