package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.theme.AvenirNext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    audios: List<Audio>,
    onBack: () -> Unit,
    bitmapCache: MutableMap<Long, Bitmap?>
) {

    val albums = audios.map { it.album }.distinct().sorted()

    val albumArtist = audios.groupBy { it.album }.mapValues { (_, tracks) ->
        tracks.joinToString(", ") { it.artist }
    }


    val selectedAlbum = remember { mutableStateOf<String?>(null) }
    val albumSongs = remember { mutableStateOf<List<Audio>>(emptyList()) }

    LaunchedEffect(selectedAlbum) {
        if (selectedAlbum.value != null) {
            albumSongs.value = audios.filter { it.album == selectedAlbum.value }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        if (selectedAlbum.value != null) {
            AlbumSongsScreen(
                album = selectedAlbum.value!!,
                audios = albumSongs.value,
                onBack = { selectedAlbum.value = null },
                onPlayAll = { /* Handle play all */ },
                onPlaySong = { /* Handle play song */ },
                bitmapCache = bitmapCache
            )
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(albums) { album ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedAlbum.value = album
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_album), // Your custom album icon
                            contentDescription = "Album",
                            modifier = Modifier
                                .size(58.dp)
                                .padding(end = 12.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = album,
                                fontFamily = AvenirNext,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = albumArtist[album] ?: "-",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = AvenirNext,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 0.8.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AlbumListScreenPreview() {
    val sampleAudios = listOf(
        Audio(
            id = 1L,
            title = "Song 1",
            artist = "Artist A",
            album = "Album X",
            duration = 240000,
            uri = android.net.Uri.parse("content://media/external/audio/media/1")
        ),
    )

    AlbumListScreen(
        audios = sampleAudios,
        onBack = {},
        bitmapCache = mutableMapOf()
    )
}
