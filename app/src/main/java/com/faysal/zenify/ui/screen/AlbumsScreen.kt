package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.UiTypes
import com.faysal.zenify.ui.states.MusicScreen
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.sampleAudios
import com.faysal.zenify.ui.viewModels.MusicViewModel

@OptIn(UnstableApi::class)
@Composable
fun AlbumsScreen(
    audios: List<Audio>,
    bitmapCache: MutableMap<Long, Bitmap?>,
    viewModel: MusicViewModel
) {

    val backStack = viewModel.backStack
    val currentScreen = backStack.last()

    BackHandler(enabled = backStack.size > 1) {
        viewModel.pop()
    }

    when (currentScreen) {
        is MusicScreen.AlbumList -> {

            val albums = audios.map { it.album }.distinct().sorted()

            val albumArtist = audios.groupBy { it.album }.mapValues { (_, tracks) ->
                tracks.distinct().joinToString(", ") { it.artist }
            }

            Column(modifier = Modifier.fillMaxSize()) {
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
                                    backStack.add(MusicScreen.AlbumSongs(album))
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_album),
                                contentDescription = "Album",
                                modifier = Modifier
                                    .size(58.dp)
                                    .padding(end = 12.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = album,
                                    fontFamily = AvenirNext,
                                    color = Color.White,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = albumArtist[album] ?: "-",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = AvenirNext,
                                    maxLines = 1,
                                    color = Color.White.copy(alpha = 0.6f),
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        is MusicScreen.AlbumSongs -> {
            val album = currentScreen.album
            val songs = audios.filter { it.album == album }

            CommonSongListScreen(
                title = album,
                audios = songs,
                onBack = { backStack.removeAt(backStack.lastIndex) },
                uiTypes = UiTypes.Album,
                onPlayAll = {
                    viewModel.setPlaylist(it)
                    viewModel.playAudio(it.first())
                },
                onPlaySong = {
                    viewModel.playAudio(it)
                },
                bitmapCache = bitmapCache
            )
        }

        else -> Unit // optional: add handling for other screens if needed
    }
}



@Preview(showBackground = true)
@Composable
fun AlbumListScreenPreview() {

    AlbumsScreen(
        audios = sampleAudios,
        bitmapCache = mutableMapOf(),
        viewModel = rememberFakeMusicViewModel()
    )
}
