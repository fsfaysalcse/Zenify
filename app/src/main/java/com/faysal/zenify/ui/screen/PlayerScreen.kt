package com.faysal.zenify.ui.screen

import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.BlindingLights
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.MusicGradient
import com.faysal.zenify.ui.theme.MusicSecondaryColor
import com.faysal.zenify.ui.components.GestureMusicButton
import com.faysal.zenify.ui.components.LyricsCaption
import com.faysal.zenify.ui.components.LyricsHeaderBar
import com.faysal.zenify.ui.components.ZenWaveSeekBar
import com.faysal.zenify.ui.util.getEmbeddedCover
import com.faysal.zenify.ui.util.sampleAudios


@Composable
fun PlayerScreen(
    isPlaying: Boolean,
    currentAudio: Audio,
    onPlayPauseClick: () -> Unit,
    onMinimizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(false) }
    var currentSong by remember { mutableIntStateOf(0) }

    var progress by remember { mutableFloatStateOf(0.3f) }

    var currentTime by remember { mutableLongStateOf(33000) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = MusicGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
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
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier.size(300.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        val musicCover = getEmbeddedCover(context = context, uri = currentAudio.uri)
                        val imageBitmap = musicCover?.asImageBitmap()

                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Music Note",
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(CircleShape)
                                    .shadow(
                                        elevation = 20.dp,
                                        shape = CircleShape,
                                        ambientColor = Color(0xFFFFFFFF),
                                        spotColor = Color(0xFF000000)
                                    )
                            )
                        }


                        GestureMusicButton(
                            isPlaying = isPlaying,
                            onPlayPause = { isPlaying = it },
                            onLongPress = { /* Handle long press */ },
                            onSwipe = { direction -> println("Swiped: $direction") },
                            onHold = { direction -> println("Held: $direction") }
                        )
                    }


                    // Song Title and Artist

                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(0.8f),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = currentAudio.title,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontFamily = AvenirNext,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Text(
                                text = currentAudio.artist,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontFamily = AvenirNext,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        //Bookmark Icon
                        IconButton(
                            onClick = { /* Handle bookmark click */ },
                            modifier = Modifier
                                .weight(0.2f)
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_bookmark),
                                contentDescription = "Bookmark",
                                modifier = Modifier.size(27.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }

                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ZenWaveSeekBar(
                            modifier = Modifier.fillMaxWidth(),
                            progress = progress,
                            onProgressChange = { progress = it },
                            activeWaveColor = Color(0xFFFFFFFF),
                            inactiveWaveColor = Color.White.copy(alpha = 0.1f),
                            durationMs = 210000L,
                            barWidth = 5.dp,
                            barSpacing = 2.dp,
                            maxBarHeight = 20.dp
                        )
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
                            onClick = { /* Handle shuffle */ },
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
                            onClick = { /* Handle repeat */ },
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
                            onClick = { /* Handle share */ },
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
                            onClick = { /* Handle queue */ },
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

        val configuration = LocalConfiguration.current


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

        val lyricsBackgroundColor by animateColorAsState(
            targetValue = if (isFullScreen) {
                MusicSecondaryColor.copy(alpha = 1f)
            } else {
                MusicSecondaryColor.copy(alpha = 0.5f)
            }
        )


        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animateLyricsHeight)
                    .padding(horizontal = animateLyricsPadding)
                    .offset(y = animateLyricsOffsetY)
                    .background(
                        color = MusicSecondaryColor,
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

                LyricsCaption(
                    song = BlindingLights,
                    currentTimeMs = currentTime,
                    isPlaying = isPlaying,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {

    PlayerScreen(
        isPlaying = true,
        currentAudio = sampleAudios.first(),
        onPlayPauseClick = { /* Handle play/pause */ },
        onMinimizeClick = { /* Handle minimize */ },
        modifier = Modifier.fillMaxSize()
    )
}