package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.repository.FakeAudioRepository
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.components.CustomTabBar
import com.faysal.zenify.ui.components.MusicBottomSheet
import com.faysal.zenify.ui.components.SearchBar
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.ZenifyTheme
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
    val pagerState = rememberPagerState { 4 }
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Songs", "Albums", "Playlists", "Folder")

    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()


    val audios by viewModel.audios.collectAsState()

    val bitmapCache = remember { mutableStateMapOf<Long, Bitmap?>() }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { newValue ->
            // Only allow hiding if not playing
            newValue != SheetValue.Hidden || !isPlaying
        }
    )

    LaunchedEffect(currentAudio, isPlaying) {
        if (isPlaying && currentAudio != null && sheetState.currentValue == SheetValue.Hidden) {
            sheetState.partialExpand()
        } else if (!isPlaying && sheetState.currentValue != SheetValue.Hidden) {
            sheetState.hide()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {


        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AvenirNext,
                fontSize = 32.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            SearchBar(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            CustomTabBar(
                tabs = tabs,
                selectedTabIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    2 -> Text("Song list here", modifier = Modifier.padding(16.dp))
                    0 -> {
                        AlbumListScreen(
                            audios = audios,
                            onBack = {},
                            bitmapCache = bitmapCache
                        )
                    }

                    else -> Text("Else list here", modifier = Modifier.padding(16.dp))
                }
            }
        }

//        MusicBottomSheet(
//            isPlaying = isPlaying,
//            currentAudio = currentAudio,
//            onPlayPauseClick = {
////                    if (isPlaying) viewModel.playPause()
////                    else viewModel.playAudio(currentAudio)
//            },
//            sheetState = sheetState,
//            onDismissRequest = {
//                coroutineScope.launch {
//                    sheetState.hide()
//                }
//            }
//        )
    }
}


@OptIn(UnstableApi::class)
@Composable
fun rememberFakeMusicViewModel(): MusicViewModel {
    val context = LocalContext.current

    val repository = FakeAudioRepository()
    val serviceConect = MusicServiceConnection(context)
    return remember {
        object : MusicViewModel(serviceConect, GetAudiosUseCase(repository)) {
            init {
                loadAudios()
            }
        }
    }
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
