package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.repository.FakeAudioRepository
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.components.CustomTabBar
import com.faysal.zenify.ui.components.MiniPlayer
import com.faysal.zenify.ui.components.SearchBar
import com.faysal.zenify.ui.states.MusicScreen
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.ZenifyTheme
import com.faysal.zenify.ui.theme.blackToNeveBlue
import com.faysal.zenify.ui.viewModels.MusicViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState(initialPage = 0) { 4 }
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Explore","Tracks", "Albums", "Artist", "Folder")

    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()

    val audios by viewModel.audios.collectAsState()
    val bitmapCache = remember { mutableStateMapOf<Long, Bitmap?>() }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var showFullScreenPlayer by remember { mutableStateOf(false) }

    val backStack = viewModel.backStack.last()

    // Push correct screen based on current tab
    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> viewModel.resetBackStack(MusicScreen.Overview)
            1 -> viewModel.resetBackStack(MusicScreen.SongsList)
            2 -> viewModel.resetBackStack(MusicScreen.AlbumList)
            3 -> viewModel.resetBackStack(MusicScreen.ArtistList)
            4 -> viewModel.resetBackStack(MusicScreen.FolderList)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (currentAudio != null) 88.dp else 0.dp)
        ) {
            val hideSearch by animateDpAsState(
                targetValue = if (backStack.hideHomeScreen()) 0.dp else 130.dp,
                animationSpec = tween(durationMillis = 200)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hideSearch)
            ) {
                SearchBar(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                CustomTabBar(
                    tabs = tabs,
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { index ->
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }
                )
            }



            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> OverviewScreen(audios, bitmapCache, viewModel)
                    1 -> SongsScreen(audios, bitmapCache, viewModel)
                    2 -> AlbumsScreen(audios, bitmapCache, viewModel)
                    3 -> ArtistsScreen(audios, bitmapCache, viewModel)
                    4 -> FoldersScreen(audios, bitmapCache, viewModel)
                }
            }
        }

        // Mini Player - Always visible when currentAudio is not null
        if (currentAudio != null) {
            MiniPlayer(
                isPlaying = isPlaying,
                currentAudio = currentAudio!!,
                onPlayPauseClick = {
                    if (isPlaying) viewModel.playPause()
                    else viewModel.playAudio(currentAudio!!)
                },
                onExpandClick = {
                    showFullScreenPlayer = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
            )
        }

        // Full Screen Bottom Sheet - Only shows when explicitly requested
        if (showFullScreenPlayer && currentAudio != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showFullScreenPlayer = false
                },
                sheetState = bottomSheetState,
                containerColor = Color.Black,
                contentColor = Color.Black,
                shape = RectangleShape, // full-screen without rounded corners
                dragHandle = null // no drag handle
            ) {
                PlayerScreen(
                 /*   isPlaying = isPlaying,
                    currentAudio = currentAudio!!,
                    onPlayPauseClick = {
                        if (isPlaying) viewModel.playPause()
                        else viewModel.playAudio(currentAudio!!)
                    },*/
                    onMinimizeClick = {
                        showFullScreenPlayer = false
                    },
                    viewModel = viewModel,
                )
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun rememberFakeMusicViewModel(): MusicViewModel {
    val context = LocalContext.current

    // Provide dependencies (FakeAudioRepository, MusicServiceConnection) inside a factory or DI if needed
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = FakeAudioRepository()
                val serviceConnect = MusicServiceConnection(context)
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(
                    serviceConnect, GetAudiosUseCase(repository),
                    SavedStateHandle()
                ) as T
            }
        }
    }

    val viewModel: MusicViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        viewModel.loadAudios()
    }

    return viewModel
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ZenifyTheme {
        HomeScreen(
            viewModel = rememberFakeMusicViewModel()
        )
    }
}