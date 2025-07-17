package com.faysal.zenify.ui.screen

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.ui.components.GestureMusicButton
import com.faysal.zenify.ui.components.RhythmTimeline
import com.faysal.zenify.ui.states.GestureAction
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.ImageColors
import com.faysal.zenify.ui.util.extractColorsFromImage
import com.faysal.zenify.ui.util.extractColorsFromUri
import com.faysal.zenify.ui.util.getEmbeddedCover
import com.faysal.zenify.ui.viewModels.MusicViewModel


@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    onMinimizeClick: () -> Unit,
    viewModel: MusicViewModel
) {


    val context = LocalContext.current


    val duration by viewModel.duration.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()


    var progress by remember { mutableFloatStateOf(0f) }
    var currentTime by remember { mutableLongStateOf(0L) }
    var remainingTime by remember { mutableLongStateOf(0L) }


    LaunchedEffect(currentPosition, duration) {
        currentTime = currentPosition
        remainingTime = duration - currentPosition
        progress = if (duration > 0) currentPosition.toFloat() / duration else 0f

        Log.d("PlaybackXD", "Played: ${currentTime}ms")
        Log.d("PlaybackXD", "Remaining: ${remainingTime}ms")
        Log.d("PlaybackXD", "Progress: ${"%.2f".format(progress)}")
    }

    val musicCover =
        getEmbeddedCover(context = context, uri = currentAudio?.uri)
    val imageBitmap = musicCover?.asImageBitmap()

    var imageColors by remember { mutableStateOf(ImageColors()) }

    LaunchedEffect(imageBitmap, currentAudio?.uri) {
        imageColors = when {
            imageBitmap != null -> extractColorsFromImage(imageBitmap)
            currentAudio?.uri != null -> extractColorsFromUri(context, currentAudio?.uri.toString())
            else -> ImageColors()
        }
    }

    val vibrant = imageColors.vibrant.takeIf { it != Color.Unspecified } ?: Color(0xFF6A4ACB)
    val muted = imageColors.muted.takeIf { it != Color.Unspecified } ?: Color(0xFF9C6BFF)
    val dominant = imageColors.dominant.takeIf { it != Color.Unspecified } ?: Color(0xFF4C3E9A)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            dominant.copy(alpha = 1f),
            vibrant.copy(alpha = 0.8f),
            muted.copy(alpha = 0.9f),
        )
    )



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
    ) {

        //Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onMinimizeClick,
                modifier = Modifier.size(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = "Back",
                    modifier = Modifier.size(27.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            // Title
            Text(
                text = "Now Playing",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = AvenirNext,
                fontWeight = FontWeight.Medium
            )

            // More Options Button
            IconButton(
                onClick = { /* Handle more options */ },
                modifier = Modifier.size(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "More Options",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Player Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = 0.3f)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier.size(360.dp),
                        contentAlignment = Alignment.Center
                    ) {


                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Music Note",
                                modifier = Modifier
                                    .size(320.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .shadow(
                                        elevation = 20.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        ambientColor = Color(0xFFFFFFFF),
                                        spotColor = Color(0xFF000000)
                                    )
                            )
                        }
                    }


                    // Song Title and Artist

                        Column(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = currentAudio?.title ?: "Unknown",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontFamily = AvenirNext,
                                lineHeight = 28.sp,
                                maxLines = 1,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Text(
                                text = currentAudio?.artist ?: "Unknown",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontFamily = AvenirNext,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )

                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RhythmTimeline(
                            modifier = Modifier.fillMaxWidth(),
                            currentPositionMs = currentPosition,
                            durationMs = viewModel.duration.collectAsState().value,
                            onSeek = { newPosition -> viewModel.seekTo(newPosition) }
                        )


                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_left),
                                contentDescription = "Left",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )


                            GestureMusicButton(
                                modifier = Modifier.padding(vertical = 20.dp),
                                isPlaying = isPlaying,
                                onPlayPause = {
                                    viewModel.playPause()
                                },
                                coverPrimary = imageColors.vibrant,
                                onLongPress = { /* Handle long press */ },
                                onSwipe = { direction ->

                                    when (direction) {
                                        GestureAction.Right -> {
                                            viewModel.playNext()
                                            Log.d("GestureAction", "PlayerScreen: Right")
                                        }

                                        GestureAction.Left -> {
                                            viewModel.playPrevious()
                                            Log.d("GestureAction", "PlayerScreen: Left")
                                        }

                                        GestureAction.Up -> {
                                            Log.d("GestureAction", "PlayerScreen: UP")
                                        }

                                        GestureAction.Down -> {
                                            Log.d("GestureAction", "PlayerScreen: Down")
                                        }

                                        else -> Unit
                                    }
                                },
                                onHold = { direction ->
                                    when (direction) {
                                        GestureAction.Hold.Left -> {
                                            viewModel.playPrevious()
                                            Log.d("GestureAction", "PlayerScreen: Hold Left")
                                        }

                                        GestureAction.Hold.Right -> {
                                            viewModel.playNext()
                                            Log.d("GestureAction", "PlayerScreen: Hold Right")
                                        }

                                        GestureAction.Hold.Up -> {
                                            Log.d("GestureAction", "PlayerScreen: Hold Up")
                                        }

                                        GestureAction.Hold.Down -> {
                                            Log.d("GestureAction", "PlayerScreen: Hold Down")
                                        }
                                    }
                                }
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_right),
                                contentDescription = "Right",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )

                        }

                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Shuffle Button
                        IconButton(
                            onClick = {
                                viewModel.toggleShuffle()
                                Log.d(
                                    "PlayerScreen",
                                    "Shuffle toggled: ${viewModel.isShuffleEnabled.value}"
                                )
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_shuffle),
                                contentDescription = "Shuffle",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }

                        // Repeat Button
                        IconButton(
                            onClick = {
                                viewModel.toggleRepeat()
                                Log.d(
                                    "PlayerScreen",
                                    "Repeat toggled: ${viewModel.isRepeatEnabled.value}"
                                )
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_repeat),
                                contentDescription = "Repeat",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = {
                                Log.d("PlayerScreen", "Share clicked")
                                // Handle share action here
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = "Share",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }

                        //Queue Button
                        IconButton(
                            onClick = {
                                Log.d("PlayerScreen", "Queue clicked")
                                // Handle queue action here
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_queue),
                                contentDescription = "Queue",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }

                }
            }
        }

        // Lyric View

        var isFullScreen by remember { mutableStateOf(false) }

        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        val animateLyricsHeight by animateDpAsState(
            targetValue = if (isFullScreen) screenHeight else 100.dp,
            animationSpec = tween(durationMillis = 300),
            label = "lyrics_height_animation"
        )

        val animateLyricsOffsetY by animateDpAsState(
            targetValue = if (isFullScreen) 0.dp else screenHeight - 100.dp,
            animationSpec = tween(durationMillis = 300),
            label = "lyrics_offset_animation"
        )

        val animateLyricsPadding by animateDpAsState(
            targetValue = if (isFullScreen) 0.dp else 16.dp,
            animationSpec = tween(durationMillis = 300),
            label = "lyrics_padding_animation"
        )



        /*Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animateLyricsHeight)
                    .padding(horizontal = animateLyricsPadding)
                    .offset(y = animateLyricsOffsetY)
                    .background(
                        color = imageColors.vibrant,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {

                LyricsHeaderBar(
                    onShareClick = {
                        Log.d("LyricsHeader", "Share clicked")
                    },
                    onFullScreenClick = {
                        isFullScreen = !isFullScreen
                        Log.d("LyricsHeader", "FullScreen toggled: $isFullScreen")
                    }
                )
            }
        }*/
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {

    PlayerScreen(
        onMinimizeClick = { /* Handle minimize */ },
        viewModel = rememberFakeMusicViewModel()
    )
}