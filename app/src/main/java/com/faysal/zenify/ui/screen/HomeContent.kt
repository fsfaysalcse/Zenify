package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.ui.components.MiniPlayer
import com.faysal.zenify.ui.components.ModernCustomTabBar
import com.faysal.zenify.ui.components.ModernSearchBar
import com.faysal.zenify.ui.mock.rememberFakeMusicViewModel
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
    val bitmapCache = remember { mutableStateMapOf<String, Bitmap?>() }

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
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                tonalElevation = 8.dp,
                shape = RectangleShape,
                properties = ModalBottomSheetProperties(
                    shouldDismissOnBackPress = true,
                ),
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
