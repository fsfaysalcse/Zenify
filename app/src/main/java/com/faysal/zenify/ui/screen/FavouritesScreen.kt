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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.ui.components.AudioItem
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.viewModels.FavouriteViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun FavouritesScreen(
    bitmapCache: MutableMap<String, Bitmap?>,
    favouriteViewModel: FavouriteViewModel = koinViewModel()
) {
    val favouriteUiState by favouriteViewModel.uiState.collectAsState()
    val favourites by favouriteViewModel.favourites.collectAsState()
    val currentAudio by favouriteViewModel.currentAudio.collectAsState()
    val isPlaying by favouriteViewModel.isPlaying.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(favouriteUiState.error) {
        favouriteUiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            favouriteViewModel.clearError()
        }
    }

    LaunchedEffect(favouriteUiState.message) {
        favouriteUiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            favouriteViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                favouriteUiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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

                favourites.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(60.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
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
                                fontFamily = AvenirNext,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Start adding songs to your favourites and they'll appear here. Tap the heart icon on any song to save it!",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = AvenirNext,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        item {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Favorite,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "My Favourites",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontSize = 32.sp,
                                            fontFamily = AvenirNext,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${favourites.size} ${if (favourites.size == 1) "song" else "songs"}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontSize = 16.sp,
                                            fontFamily = AvenirNext,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            FilledTonalButton(
                                                onClick = { favouriteViewModel.playAllFavourites() },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                contentPadding = PaddingValues(vertical = 14.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Play All",
                                                    fontFamily = AvenirNext,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            FilledTonalButton(
                                                onClick = { favouriteViewModel.shuffleFavourites() },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                contentPadding = PaddingValues(vertical = 14.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_shuffle),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Shuffle",
                                                    fontFamily = AvenirNext,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            FilledTonalButton(
                                                onClick = { favouriteViewModel.addFavouritesToQueue() },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                contentPadding = PaddingValues(vertical = 14.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_queue),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Queue",
                                                    fontFamily = AvenirNext,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        items(
                            items = favourites,
                            key = { it.audio.id }
                        ) { favourite ->
                            val isFavourite by favouriteViewModel.isFavourite(favourite.audio.id)
                                .collectAsState(initial = true)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (currentAudio?.id == favourite.audio.id && isPlaying) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    } else {
                                        Color.Transparent
                                    }
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                AudioItem(
                                    audio = favourite.audio,
                                    bitmapCache = bitmapCache,
                                    isFavorite = isFavourite,
                                    isCurrentlyPlaying = currentAudio?.id == favourite.audio.id && isPlaying,
                                    onFavoriteClick = {
                                        favouriteViewModel.removeFromFavourites(favourite.audio.id)
                                    },
                                    onClick = {
                                        favouriteViewModel.playFavourite(favourite)
                                    }
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

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}