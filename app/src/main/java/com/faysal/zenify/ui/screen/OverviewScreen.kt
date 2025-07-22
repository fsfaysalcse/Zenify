package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.domain.model.AlbumInfo
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.components.AlbumCard
import com.faysal.zenify.ui.components.AudioItem
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.NavFont
import com.faysal.zenify.ui.util.sampleAudios
import com.faysal.zenify.ui.viewModels.MusicViewModel

@UnstableApi
@Composable
fun OverviewScreen(
    audios: List<Audio>,
    bitmapCache: MutableMap<Long, Bitmap?>,
    viewModel: MusicViewModel
) {
    // Get unique albums and their info
    val albums = audios.groupBy { it.album }
        .map { (album, tracks) ->
            AlbumInfo(
                name = album,
                artist = tracks.distinctBy { it.artist }.joinToString(", ") { it.artist },
                trackCount = tracks.size,
                tracks = tracks,
                uri = tracks.first().uri
            )
        }
        .sortedBy { it.name }
        .take(10) // Show top 10 albums

    // Get top songs (can be filtered by play count, recently played, etc.)
    val topSongs = audios.sortedBy { it.title }.take(20) // Simplified sorting

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
          //  Spacer(modifier = Modifier.height(16.dp))

            // Albums Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Albums",
                    fontSize = 23.sp,
                    letterSpacing = 3.sp,
                    fontFamily = NavFont,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = {
                        // Navigate to full albums screen
                        //   viewModel.navigateToAlbums()
                    }
                ) {
                    Text(
                        text = "View All",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontFamily = AvenirNext,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            // Albums Horizontal List
            LazyRow(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Spacer(modifier = Modifier.width(12.dp))
                }
                items(albums) { album ->

                    AlbumCard(
                        album = album,
                        onPlayAlbum = {
                             viewModel.setPlaylist(album.tracks)
                             viewModel.playAudio(album.tracks.first())
                        },
                        onAlbumClick = {
                            // Navigate to album details
                            //  viewModel.navigateToAlbumSongs(album.name)
                        },
                        bitmapCache = bitmapCache
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Top Songs Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Songs",
                    fontSize = 23.sp,
                    letterSpacing = 3.sp,
                    fontFamily = NavFont,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = {
                        // Navigate to all songs
                        //  viewModel.navigateToAllSongs()
                    }
                ) {
                    Text(
                        text = "View All",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontFamily = AvenirNext,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Top Songs List
        items(topSongs) { audio ->

            AudioItem(
                audio = audio,
                bitmapCache = bitmapCache,
                onClick = {
                   viewModel.playAudio(audio)
                }
            )

            HorizontalDivider(
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



