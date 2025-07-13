package com.faysal.zenify.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.MusicGradient
import com.faysal.zenify.ui.widgets.GestureMusicButton
import com.faysal.zenify.ui.widgets.GradientVolumeIndicator
import com.faysal.zenify.ui.widgets.ZenWaveSeekBar

data class Song(
    val title: String,
    val artist: String,
    val duration: String
)

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {


    var isPlaying by remember { mutableStateOf(false) }
    var currentSong by remember { mutableIntStateOf(0) }

    var progress by remember { mutableFloatStateOf(0.3f) }

    val songs = listOf(
        Song("Blinding Lights(Remix)", "The Weekend", "3:45"),
        Song("Digital Rain", "Neon Pulse", "4:12"),
        Song("Cosmic Waves", "Stellar Drift", "5:03"),
        Song("Urban Lights", "City Glow", "3:28")
    )


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
                onClick = { /* Handle back navigation */ },
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
                text = "Playing Recommended Tracks",
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

                        Image(
                            painter = painterResource(id = com.faysal.zenify.R.drawable.music_cover),
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
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(0.8f),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = songs[currentSong].title,
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
                                text = songs[currentSong].artist,
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


                    Spacer(modifier = Modifier.height(15.dp))

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(
                        topStart = 9.dp,
                        topEnd = 9.dp
                    )
                )
                .padding(all = 16.dp)
        ) {

        }


    }
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    PlayerScreen()
}