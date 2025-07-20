package com.faysal.zenify.ui.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.formatDuration
import com.faysal.zenify.ui.util.getEmbeddedCover
import com.faysal.zenify.ui.util.sampleAudios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AudioItem(
    audio: Audio,
    bitmapCache: MutableMap<Long, Bitmap?>,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember(audio.id) { mutableStateOf(true) }

    LaunchedEffect(audio.uri) {
        if (!bitmapCache.contains(audio.id)) {
            isLoading = true
            val art = withContext(Dispatchers.IO) {
                getEmbeddedCover(context, audio.uri)
            }
            bitmapCache[audio.id] = art
            isLoading = false
        } else {
            isLoading = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art or Placeholder
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            when {
                bitmapCache[audio.id] != null -> {
                    Image(
                        bitmap = bitmapCache[audio.id]!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                isLoading -> {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(28.dp)
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.default_cover),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Artist
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = audio.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                fontFamily = AvenirNext,
                color = Color.White,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = audio.artist,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = AvenirNext,
                overflow = TextOverflow.Ellipsis
            )
        }

        // More Options
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "More options",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/*
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AudioItemPreview() {
    val bitmapCache = mutableMapOf<Long, Bitmap?>()

    AudioItem(
        audio = sampleAudios.first(),
        bitmapCache = bitmapCache,
        onClick = {}
    )
}*/
