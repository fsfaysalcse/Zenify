package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.components.AudioItem

@Composable
fun TrackListScreen(
    audios: List<Audio>,
    onBack: () -> Unit,
    onPlaySong: (Audio) -> Unit,
    bitmapCache: MutableMap<Long, Bitmap?>
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tracks",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }
        LazyColumn {
            items(audios) { audio ->
                AudioItem(
                    audio = audio,
                    bitmapCache = bitmapCache,
                    onClick = { onPlaySong(audio) }
                )
            }
        }
    }
}