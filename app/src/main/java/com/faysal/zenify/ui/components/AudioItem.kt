package com.faysal.zenify.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.formatDuration
import com.faysal.zenify.ui.util.getEmbeddedCover
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            bitmapCache[audio.id] != null -> {
                Image(
                    bitmap = bitmapCache[audio.id]!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }

            
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(14.dp),
                    strokeWidth = 2.dp
                )
            }
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.default_cover),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                audio.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                fontFamily = AvenirNext,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                audio.artist,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                fontFamily = AvenirNext,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                formatDuration(audio.duration),
                fontFamily = AvenirNext,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}