package com.faysal.zenify.ui.components

import android.graphics.RenderEffect
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toAndroidTileMode
import androidx.compose.ui.unit.dp
import com.faysal.zenify.ui.theme.ZenifyPrimary

@Composable
fun GlowingImage(imageBitmap: ImageBitmap, glowColor: Color = ZenifyPrimary) {
    Box(
        modifier = Modifier
            .size(280.dp)
            .graphicsLayer {
                shadowElevation = 30f
                shape = CircleShape
                clip = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    renderEffect = RenderEffect
                        .createBlurEffect(40f, 40f, TileMode.Clamp.toAndroidTileMode())
                        .asComposeRenderEffect()
                }
            }
            .background(glowColor.copy(alpha = 0.6f), CircleShape)
            .padding(8.dp)
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Music Note",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}
