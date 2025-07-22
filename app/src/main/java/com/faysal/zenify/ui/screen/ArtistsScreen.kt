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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.UiTypes
import com.faysal.zenify.ui.mock.rememberFakeMusicViewModel
import com.faysal.zenify.ui.states.MusicScreen
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.sampleAudios
import com.faysal.zenify.ui.viewModels.MusicViewModel

@OptIn(UnstableApi::class)
@Composable
fun ArtistsScreen(
    audios: List<Audio>,
    bitmapCache: MutableMap<String, Bitmap?>,
    viewModel: MusicViewModel
) {

    val backStack = viewModel.backStack
    val currentScreen = backStack.last()

    BackHandler(enabled = backStack.size > 1) {
        viewModel.pop()
    }

    when (currentScreen) {
        is MusicScreen.ArtistList -> {

            val artists: List<Triple<String, Int, Int>> = audios
                .groupBy { it.artist }
                .map { (artist, tracks) ->
                    val albumCount = tracks.map { it.album }.distinct().size
                    val trackCount = tracks.size
                    Triple(artist, albumCount, trackCount)
                }
                .sortedBy { it.first.lowercase() }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {

                artists.forEachIndexed { index, artist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                backStack.add(MusicScreen.ArtistSongs(artist.first))
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_artist),
                            contentDescription = "Album",
                            modifier = Modifier
                                .size(58.dp)
                                .padding(end = 12.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = artist.first,
                                fontFamily = AvenirNext,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )


                            val albumLabel = if (artist.second == 1) "album" else "albums"
                            val trackLabel = if (artist.third == 1) "track" else "tracks"

                            Text(
                                text = "${artist.second} $albumLabel | ${artist.third} $trackLabel",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = AvenirNext,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                        }
                    }

                    if (index < audios.lastIndex) {
                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        is MusicScreen.ArtistSongs -> {
            val artist = currentScreen.artist
            val songs = audios.filter { it.artist == artist }

            CommonSongListScreen(
                title = artist,
                audios = songs,
                onBack = { backStack.removeAt(backStack.lastIndex) },
                uiTypes = UiTypes.Artist,
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
fun ArtistsScreenScreenPreview() {

    ArtistsScreen(
        audios = sampleAudios,
        bitmapCache = mutableMapOf(),
        viewModel = rememberFakeMusicViewModel()
    )
}
