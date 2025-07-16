package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.components.AudioItem
import com.faysal.zenify.ui.util.sampleAudios
import com.faysal.zenify.ui.viewModels.MusicViewModel

@UnstableApi
@Composable
fun SongsScreen(
    audios: List<Audio>,
    bitmapCache: MutableMap<Long, Bitmap?>,
    viewModel: MusicViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(audios) { selectAudio ->
                AudioItem(
                    audio = selectAudio,
                    bitmapCache = bitmapCache,
                    onClick = {
                        viewModel.setPlaylist(audios)
                        viewModel.playAudio(selectAudio)
                    }
                )
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Preview(showBackground = true)
@Composable
fun SongsScreenPreview() {
    SongsScreen(
        audios = sampleAudios,
        bitmapCache = mutableMapOf(),
        viewModel = rememberFakeMusicViewModel()
    )
}
