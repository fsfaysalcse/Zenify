package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.components.AudioItem
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.sampleAudios

@Composable
fun CommonSongListScreen(
    title : String,
    audios: List<Audio>,
    onBack: () -> Unit,
    onPlayAll: (List<Audio>) -> Unit,
    onPlaySong: (Audio) -> Unit,
    bitmapCache: MutableMap<Long, Bitmap?>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = AvenirNext,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Play All",
                fontFamily = AvenirNext,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onPlayAll(audios) }
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

@Preview(showBackground = true)
@Composable
fun CommonSongListScreenPreview() {
    CommonSongListScreen(
        title = "Album A",
        audios = sampleAudios,
        onBack = {},
        onPlayAll = {},
        onPlaySong = {},
        bitmapCache = mutableMapOf()
    )
}
