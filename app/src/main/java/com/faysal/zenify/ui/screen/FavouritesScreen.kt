package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.ui.components.AudioItem
import com.faysal.zenify.ui.components.FavouriteBackground
import com.faysal.zenify.ui.components.IconActionButton
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.ProductSans
import com.faysal.zenify.ui.viewModels.FavouriteViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun FavouritesScreen(
    navHostController: NavHostController? = null,
    favouriteViewModel: FavouriteViewModel = koinViewModel()
) {
    val bitmapCache = remember { mutableStateMapOf<String, Bitmap?>() }
    val favouriteUiState by favouriteViewModel.uiState.collectAsState()
    val favourites by favouriteViewModel.favourites.collectAsState()
    val currentAudio by favouriteViewModel.currentAudio.collectAsState()
    val isPlaying by favouriteViewModel.isPlaying.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(favouriteUiState.error) {
        favouriteUiState.error?.let {
            snackBarHostState.showSnackbar(it)
            favouriteViewModel.clearError()
        }
    }

    LaunchedEffect(favouriteUiState.message) {
        favouriteUiState.message?.let {
            snackBarHostState.showSnackbar(it)
            favouriteViewModel.clearMessage()
        }
    }

    FavouriteBackground(
        primaryColor = Color(0xFFFF6B6B),
        secondaryColor = Color(0xFFFF8E8E),
        accentColor = Color(0xFFFFB3B3)
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    favouriteUiState.isLoading -> LoadingView()

                    favourites.isEmpty() -> EmptyFavouritesView()

                    else -> FavouriteListView(
                        navHostController = navHostController,
                        favourites = favourites,
                        bitmapCache = bitmapCache,
                        currentAudioId = currentAudio?.id,
                        isPlaying = isPlaying,
                        onPlayAll = favouriteViewModel::playAllFavourites,
                        onShuffle = favouriteViewModel::shuffleFavourites,
                        onQueue = favouriteViewModel::addFavouritesToQueue,
                        onRemoveFavourite = favouriteViewModel::removeFromFavourites,
                        onPlayFavourite = favouriteViewModel::playFavourite,
                        favouriteViewModel = favouriteViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading your favourites...",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = AvenirNext,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyFavouritesView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Favourites Yet",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            fontFamily = ProductSans,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Start adding songs to your favourites and they'll appear here. Tap the heart icon on any song to save it!",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontFamily = ProductSans,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}


@Composable
private fun FavouriteListView(
    favouriteViewModel: FavouriteViewModel,
    navHostController: NavHostController?,
    favourites: List<FavouriteAudio>,
    bitmapCache: MutableMap<String, Bitmap?>,
    currentAudioId: String?,
    isPlaying: Boolean,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onQueue: () -> Unit,
    onRemoveFavourite: (String) -> Unit,
    onPlayFavourite: (FavouriteAudio) -> Unit
) {
    val songLabel by remember(favourites.size) {
        derivedStateOf { if (favourites.size > 1) "songs" else "song" }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        stickyHeader {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                LottieHeader(
                    count = favourites.size,
                    label = songLabel,
                    onBackClick = {
                        navHostController?.navigateUp()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconActionButton(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.ic_play),
                        text = "Play All",
                        onClick = onPlayAll,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconActionButton(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.ic_shuffle),
                        text = "Shuffle",
                        onClick = onShuffle,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconActionButton(
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.ic_queue),
                        text = "Queue",
                        onClick = onQueue,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        items(favourites, key = { it.audio.id }) { favourite ->
            val isFavourite by favouriteViewModel.isFavourite(favourite.audio.id)
                .collectAsState(initial = true)

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentAudioId == favourite.audio.id && isPlaying) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.0f)
                    } else Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                AudioItem(
                    audio = favourite.audio,
                    bitmapCache = bitmapCache,
                    isFavorite = isFavourite,
                    isCurrentlyPlaying = currentAudioId == favourite.audio.id && isPlaying,
                    onFavoriteClick = { onRemoveFavourite(favourite.audio.id) },
                    onClick = { onPlayFavourite(favourite) }
                )
            }

            if (favourite != favourites.last()) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .alpha(0.3f),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun LottieHeader(
    count: Int,
    label: String,
    onBackClick: () -> Unit = {},
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.favorite))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.8f),
                            Color.Black
                        )
                    )
                )
                .height(100.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "My Favourites",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = AvenirNext,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$count $label",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = AvenirNext,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onBackClick, modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@UnstableApi
@Preview
@Composable
fun PreviewFavouritesScreen() {
    // FavouritesScreen(favouriteViewModel = rememberFakeFavouriteViewModel())
    LottieHeader(
        count = 5,
        label = "songs",
        onBackClick = { /* No-op for preview */ }
    )
}
