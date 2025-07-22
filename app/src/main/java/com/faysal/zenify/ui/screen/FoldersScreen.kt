package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.UiTypes
import com.faysal.zenify.ui.states.MusicScreen
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.viewModels.MusicViewModel
import java.io.File


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
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
        is MusicScreen.FolderList -> {

            val folderSet = mutableSetOf<Pair<String, String>>()

            audios.mapNotNull { it.uri.path }
                .forEach { fullPath ->
                    val segments = fullPath.split("/").filter { it.isNotEmpty() }
                    for (i in 1 until segments.size) {
                        val folderName = segments[i - 1]
                        val folderPath = "/" + segments.subList(0, i).joinToString("/")
                        folderSet.add(folderName to folderPath)
                    }
                }

            val folderList = folderSet
                .distinctBy { it.second }
                .sortedBy { it.first.lowercase() }



            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    items(folderList) { folder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    backStack.add(MusicScreen.FolderSongs(folder.second))
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_folder),
                                contentDescription = "Folder",
                                modifier = Modifier
                                    .size(58.dp)
                                    .padding(end = 12.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = folder.first,
                                    fontFamily = AvenirNext,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = folder.second,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = AvenirNext,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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

        is MusicScreen.FolderSongs -> {
            val folderPath = currentScreen.folderPath
            val songs = audios.filter { audio ->
                audio.uri.path?.let { File(it).parentFile?.path == folderPath } ?: false
            }

            CommonSongListScreen(
                title = folderPath,
                audios = songs,
                onBack = { backStack.removeAt(backStack.lastIndex) },
                uiTypes = UiTypes.Folder,
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

        else -> Unit
    }
}
