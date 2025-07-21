package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.repository.FakeAudioRepository
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.components.*
import com.faysal.zenify.ui.states.MusicScreen
import com.faysal.zenify.ui.viewModels.MusicViewModel
import kotlinx.coroutines.launch
import me.fsfaysalcse.discoverbd.ui.model.DrawerState
import org.koin.androidx.compose.koinViewModel

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun HomeContent(
    viewModel: MusicViewModel = koinViewModel(),
    drawerState: DrawerState,
    onNavigationClick: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { 5 }
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("Explore", "Tracks", "Albums", "Artist", "Folder")
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()
    val audios by viewModel.audios.collectAsState()
    val bitmapCache = remember { mutableStateMapOf<Long, Bitmap?>() }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFullScreenPlayer by remember { mutableStateOf(false) }

    val backStack = viewModel.backStack.last()

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> viewModel.resetBackStack(MusicScreen.Overview)
            1 -> viewModel.resetBackStack(MusicScreen.SongsList)
            2 -> viewModel.resetBackStack(MusicScreen.AlbumList)
            3 -> viewModel.resetBackStack(MusicScreen.ArtistList)
            4 -> viewModel.resetBackStack(MusicScreen.FolderList)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
                .padding(bottom = if (currentAudio != null) 88.dp else 0.dp)
        ) {


            AnimatedVisibility(!backStack.hideHomeScreen()) {
                Column {
                    ModernSearchBar(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        drawerState = drawerState,
                        onNavigationClick = onNavigationClick,
                        backgroundColor = Color(0x812D2D2D),
                        contentColor = Color.White
                    )

                    ModernCustomTabBar(
                        tabs = tabs,
                        selectedTabIndex = pagerState.currentPage,
                        onTabSelected = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }

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

        if (showFullScreenPlayer && currentAudio != null) {
            ModalBottomSheet(
                onDismissRequest = { showFullScreenPlayer = false },
                sheetState = bottomSheetState,
                containerColor = Color.Black,
                contentColor = Color.Black,
                shape = RectangleShape,
                dragHandle = null
            ) {
                PlayerScreen(
                    viewModel = viewModel,
                    onMinimizeClick = { showFullScreenPlayer = false }
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun rememberFakeMusicViewModel(): MusicViewModel {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = FakeAudioRepository()
                val serviceConnect = MusicServiceConnection(context)
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(
                    serviceConnect,
                    GetAudiosUseCase(repository),
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

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Preview
@Composable
fun HomeContentPreview() {
    HomeContent(
        viewModel = rememberFakeMusicViewModel(),
        drawerState = DrawerState.OPEN,
        onNavigationClick = {}
    )
}
