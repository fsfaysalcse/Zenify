package com.faysal.zenify.ui.screen

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
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
import com.faysal.zenify.ui.components.LyricsHeaderBar
import com.faysal.zenify.ui.components.RhythmTimeline
import com.faysal.zenify.ui.shaders.MUSIC_SHADER
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

    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    val musicCover = getEmbeddedCover(context = context, uri = currentAudio?.uri)
    val imageBitmap = musicCover?.asImageBitmap()

    var imageColors by remember { mutableStateOf(ImageColors()) }

    LaunchedEffect(imageBitmap, currentAudio?.uri) {
        imageColors = when {
            imageBitmap != null -> extractColorsFromImage(imageBitmap)
            currentAudio?.uri != null -> extractColorsFromUri(context, currentAudio!!.uri.toString())
            else -> ImageColors()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        BackgroundShader(imageColors)
        PlayerHeader(onMinimizeClick)
        PlayerContent(
            imageBitmap = imageBitmap,
            currentAudioTitle = currentAudio?.title ?: "Unknown",
            currentAudioArtist = currentAudio?.artist ?: "Unknown",
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            imageColors = imageColors,
            viewModel = viewModel
        )
    }
}

@Composable
private fun BackgroundShader(imageColors: ImageColors) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shader = remember { RuntimeShader(MUSIC_SHADER) }
        val time = remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            var lastTime = 0L
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    if (lastTime != 0L) {
                        val delta = (frameTimeNanos - lastTime) / 1_000_000_000f
                        time.floatValue += delta * 0.2f
                    }
                    lastTime = frameTimeNanos
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    renderEffect = RenderEffect
                        .createBlurEffect(80f, 40f, android.graphics.Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                }
                .drawWithCache {
                    val shaderBrush = ShaderBrush(shader)
                    shader.setFloatUniform("size", size.width, size.height)
                    onDrawBehind {
                        shader.setFloatUniform("time", time.floatValue)
                        shader.setColorUniform("color", imageColors.vibrant.toArgb())
                        shader.setColorUniform("color2", imageColors.dominant.toArgb())
                        drawRect(shaderBrush)
                    }
                }
        )
    }
}

@Composable
private fun PlayerHeader(onMinimizeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMinimizeClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(27.dp)
            )
        }
        Text(
            text = "Now Playing",
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = AvenirNext,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = { /* More */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@UnstableApi
@Composable
private fun PlayerContent(
    imageBitmap: ImageBitmap?,
    currentAudioTitle: String,
    currentAudioArtist: String,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    imageColors: ImageColors,
    viewModel: MusicViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Cover",
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(20.dp, RoundedCornerShape(12.dp), true)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = currentAudioTitle,
            color = Color.White,
            fontSize = 22.sp,
            fontFamily = AvenirNext,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = currentAudioArtist,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontFamily = AvenirNext,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        RhythmTimeline(
            modifier = Modifier.fillMaxWidth(),
            currentPositionMs = currentPosition,
            durationMs = duration,
            onSeek = viewModel::seekTo
        )


        Spacer(modifier = Modifier.height(40.dp))

        ControlButtons(
            coverPrimary = imageColors.vibrant,
            isPlaying = isPlaying,
            viewModel = viewModel
        )


        Spacer(modifier = Modifier.height(40.dp))

        UtilityButtons(
            viewModel = viewModel
        )

       /* Spacer(modifier = Modifier.height(20.dp))

        LyricsHeaderBar(
            onShareClick = { Log.d("LyricsHeader", "Share clicked") },
            onFullScreenClick = { Log.d("LyricsHeader", "Fullscreen toggled") },
            modifier = Modifier
                .fillMaxWidth()
                .background(imageColors.vibrant.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        )*/
    }
}

@UnstableApi
@Composable
private fun ControlButtons(
    coverPrimary: Color,
    isPlaying: Boolean,
    viewModel: MusicViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_left),
            contentDescription = "Previous",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        GestureMusicButton(
            isPlaying = isPlaying,
            onPlayPause = { viewModel.playPause() },
            coverPrimary = coverPrimary,
            onLongPress = {},
            onSwipe = {
                when (it) {
                    GestureAction.Right -> viewModel.playNext()
                    GestureAction.Left -> viewModel.playPrevious()
                    else -> {}
                }
            },
            onHold = {
                when (it) {
                    GestureAction.Hold.Left -> viewModel.playPrevious()
                    GestureAction.Hold.Right -> viewModel.playNext()
                    else -> {}
                }
            }
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_right),
            contentDescription = "Next",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@UnstableApi
@Composable
private fun UtilityButtons(viewModel: MusicViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = viewModel::toggleShuffle) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shuffle),
                contentDescription = "Shuffle",
                tint = Color.White
            )
        }
        IconButton(onClick = viewModel::toggleRepeat) {
            Icon(
                painter = painterResource(id = R.drawable.ic_repeat),
                contentDescription = "Repeat",
                tint = Color.White
            )
        }
        IconButton(onClick = { /* Share */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share",
                tint = Color.White
            )
        }
        IconButton(onClick = { /* Bookmark */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "Bookmark",
                tint = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    PlayerScreen(
        onMinimizeClick = {},
        viewModel = rememberFakeMusicViewModel()
    )
}
