package com.faysal.zenify.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.ui.states.MusicScreen
import com.faysal.zenify.ui.viewModels.MusicViewModel
import com.faysal.zenify.ui.components.PlayerBottomSheet
import org.koin.androidx.compose.koinViewModel

/*@OptIn(UnstableApi::class)
@Composable
fun MusicApp(viewModel: MusicViewModel = koinViewModel()) {
    val context = LocalContext.current

    val permissions = remember {
        mutableListOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_AUDIO
            else Manifest.permission.READ_EXTERNAL_STORAGE
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionGranted = permissions.all { result[it] == true }
        if (permissionGranted) {
            viewModel.loadAudios()
        }
    }

    LaunchedEffect(Unit) {
        val granted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (granted) {
            permissionGranted = true
            viewModel.loadAudios()
        } else {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    val audios by viewModel.audios.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()

    val bitmapCache = remember { mutableStateMapOf<Long, Bitmap?>() }

    val backStack = rememberSaveable(
        saver = listSaver(
            save = { list -> list.map { it.toString() } },
            restore = { list -> mutableStateListOf(*list.mapNotNull { MusicScreen.fromString(it) }.toTypedArray()) }
        )
    ) { mutableStateListOf<MusicScreen>(MusicScreen.TrackList) }


    val currentScreen by derivedStateOf { backStack.last() }

    fun navigateTo(screen: MusicScreen) {
        backStack.add(screen)
    }

    fun onBack() {
        if (backStack.size > 1) backStack.removeLast()
        // else you could exit app or keep on tracklist
    }

    BackHandler { onBack() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen is MusicScreen.TrackList,
                    onClick = {
                        if (currentScreen !is MusicScreen.TrackList) {
                            backStack.clear()
                            backStack.add(MusicScreen.TrackList)
                        }
                    },
                    label = { Text("Tracks") },
                    icon = { *//* add track icon if desired *//* }
                )
                NavigationBarItem(
                    selected = currentScreen is MusicScreen.AlbumList || currentScreen is MusicScreen.AlbumSongs,
                    onClick = {
                        if (currentScreen !is MusicScreen.AlbumList) {
                            backStack.clear()
                            backStack.add(MusicScreen.AlbumList)
                        }
                    },
                    label = { Text("Albums") },
                    icon = { *//* add album icon if desired *//* }
                )
                NavigationBarItem(
                    selected = currentScreen is MusicScreen.FolderList || currentScreen is MusicScreen.FolderSongs,
                    onClick = {
                        if (currentScreen !is MusicScreen.FolderList) {
                            backStack.clear()
                            backStack.add(MusicScreen.FolderList)
                        }
                    },
                    label = { Text("Folders") },
                    icon = { *//* add folder icon if desired *//* }
                )
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (val screen = currentScreen) {
                    is MusicScreen.TrackList -> TrackListScreen(
                        audios = audios,
                        onBack = ::onBack,
                        onPlaySong = { viewModel.playAudio(it) },
                        bitmapCache = bitmapCache
                    )
//                    is MusicScreen.AlbumList -> AlbumListScreen(
//                        audios = audios,
//                        onAlbumClick = { navigateTo(MusicScreen.AlbumSongs(it)) },
//                        onBack = ::onBack,
//                        bitmapCache = bitmapCache
//                    )
                    is MusicScreen.AlbumSongs -> AlbumSongsScreen(
                        album = screen.album,
                        audios = audios.filter { it.album == screen.album },
                        onBack = ::onBack,
                        onPlayAll = {
                            viewModel.setPlaylist(it)
                            viewModel.playAudio(it.first())
                        },
                        onPlaySong = { viewModel.playAudio(it) },
                        bitmapCache = bitmapCache
                    )
                    is MusicScreen.ArtistSongs -> ArtistSongsScreen(
                        artist = screen.artist,
                        audios = audios.filter { it.artist == screen.artist },
                        onBack = ::onBack,
                        onPlayAll = {
                            viewModel.setPlaylist(it)
                            viewModel.playAudio(it.first())
                        },
                        onPlaySong = { viewModel.playAudio(it) },
                        bitmapCache = bitmapCache
                    )
                    is MusicScreen.FolderList -> FolderListScreen(
                        audios = audios,
                        onFolderClick = { navigateTo(MusicScreen.FolderSongs(it)) },
                        onBack = ::onBack,
                        bitmapCache = bitmapCache
                    )
                    is MusicScreen.FolderSongs -> FolderSongsScreen(
                        folder = screen.folderPath,
                        audios = audios.filter { it.uri.pathSegments.dropLast(1).lastOrNull() == screen.folderPath },
                        onBack = ::onBack,
                        onPlayAll = {
                            viewModel.setPlaylist(it)
                            viewModel.playAudio(it.first())
                        },
                        onPlaySong = { viewModel.playAudio(it) },
                        bitmapCache = bitmapCache
                    )
                }
            }
        }
    )

    if (currentAudio != null) {
        PlayerBottomSheet(
            audio = currentAudio!!,
            bitmap = bitmapCache[currentAudio!!.id],
            viewModel = viewModel
        )
    }
}*/

/*
@Preview(showBackground = true)
@Composable
fun MusicScreenPreview() {
    MusicApp()
}
*/
