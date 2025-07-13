package com.faysal.zenify.ui.widgets


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.ui.icons.VolumeUp
import com.faysal.zenify.ui.theme.Nunito


/**
 * GradientVolumeIndicator is a customizable horizontal volume bar UI component with:
 *
 * - A leading volume icon
 * - A background bar with a soft rounded design
 * - A gradient-filled foreground indicating the current volume level
 * - A volume percentage text display
 *
 * @param volume Current volume level between 0f and 1f (inclusive)
 * @param onVolumeChange Callback when the volume changes (if made interactive later)
 * @param modifier Optional Modifier for layout customization
 */
@Composable
fun GradientVolumeIndicator(
    volume: Float,
    modifier: Modifier = Modifier,
    onVolumeChange: ((Float) -> Unit)? = null
) {
    val clampedVolume = volume.coerceIn(0f, 1f)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = VolumeUp,
            contentDescription = "Volume",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(4.dp)
                .background(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clampedVolume)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFF06B6D4)
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        Text(
            text = "${(clampedVolume * 100).toInt()}%",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontFamily = Nunito
        )
    }
}

@Preview
@Composable
fun GradientVolumeIndicatorPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFF1E293B) // Dark background for contrast
        ) {
            GradientVolumeIndicator(
                volume = 0.5f, // Example volume level
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}