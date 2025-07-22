package com.faysal.zenify.ui.screen

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.ui.components.GestureMusicButton
import com.faysal.zenify.ui.components.LyricsHeaderBar
import com.faysal.zenify.ui.components.RhythmTimeline
import com.faysal.zenify.ui.shaders.MUSIC_SHADER
import com.faysal.zenify.ui.states.GestureAction
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.ZenifyPrimary
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
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

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
            .onSizeChanged { screenSize = it }
    ) {
        val width = with(density) { screenSize.width.toDp() }
        val height = with(density) { screenSize.height.toDp() }

        BackgroundShader(imageColors)

        PortraitPlayerLayout(
            onMinimizeClick = onMinimizeClick,
            imageBitmap = imageBitmap,
            currentAudioTitle = currentAudio?.title ?: "Unknown",
            currentAudioArtist = currentAudio?.artist ?: "Unknown",
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            imageColors = imageColors,
            viewModel = viewModel,
            maxWidth = width,
            maxHeight = height
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

@UnstableApi
@Composable
private fun PortraitPlayerLayout(
    onMinimizeClick: () -> Unit,
    imageBitmap: ImageBitmap?,
    currentAudioTitle: String,
    currentAudioArtist: String,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    imageColors: ImageColors,
    viewModel: MusicViewModel,
    maxWidth: Dp,
    maxHeight: Dp
) {
    val scrollState = rememberScrollState()
    val isCompact = maxHeight < 700.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .navigationBarsPadding()
    ) {
        PlayerHeader(
            onMinimizeClick = onMinimizeClick,
            modifier = Modifier.statusBarsPadding()
        )

        PlayerContent(
            imageBitmap = imageBitmap,
            currentAudioTitle = currentAudioTitle,
            currentAudioArtist = currentAudioArtist,
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            imageColors = imageColors,
            viewModel = viewModel,
            isCompact = isCompact,
            maxWidth = maxWidth
        )
    }
}


@Composable
private fun PlayerHeader(
    onMinimizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMinimizeClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Now Playing",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = AvenirNext,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = { },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
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
    viewModel: MusicViewModel,
    isCompact: Boolean,
    maxWidth: Dp
) {
    val albumArtSize = when {
        isCompact -> min(200.dp, maxWidth * 0.5f)
        else -> min(320.dp, maxWidth * 0.75f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            if (isCompact) 16.dp else 24.dp
        )
    ) {
        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 32.dp))

        AlbumArtSection(
            imageBitmap = imageBitmap,
            maxSize = albumArtSize
        )

        PlayerControlsSection(
            currentAudioTitle = currentAudioTitle,
            currentAudioArtist = currentAudioArtist,
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            imageColors = imageColors,
            viewModel = viewModel,
            isCompact = isCompact
        )

        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 32.dp))
    }
}

@Composable
private fun AlbumArtSection(
    imageBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    maxSize: Dp = 220.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Album Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(maxSize)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
            )
        } else {
            Surface(
                modifier = Modifier
                    .size(maxSize)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                color =Color.White.copy(alpha = 0.1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.default_cover),
                    contentDescription = "No Cover",
                    tint =Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@UnstableApi
@Composable
private fun PlayerControlsSection(
    currentAudioTitle: String,
    currentAudioArtist: String,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    imageColors: ImageColors,
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            if (isCompact) 12.dp else 20.dp
        )
    ) {
        TrackInfoSection(
            title = currentAudioTitle,
            artist = currentAudioArtist,
            isCompact = isCompact
        )

        RhythmTimeline(
            modifier = Modifier.fillMaxWidth(),
            currentPositionMs = currentPosition,
            durationMs = duration,
            onSeek = viewModel::seekTo
        )

        MainControlButtons(
            coverPrimary = imageColors.vibrant,
            isPlaying = isPlaying,
            viewModel = viewModel,
            isCompact = isCompact
        )

        UtilityButtons(
            viewModel = viewModel,
            isCompact = isCompact
        )

        LyricsHeaderBar(
            onShareClick = {},
            onFullScreenClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = imageColors.muted.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        )
    }
}

@Composable
private fun TrackInfoSection(
    title: String,
    artist: String,
    isCompact: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = if (isCompact) 18.sp else 24.sp,
            fontFamily = AvenirNext,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Text(
            text = artist,
            color =Color.White.copy(alpha = 0.75f),
            fontSize = if (isCompact) 14.sp else 16.sp,
            fontFamily = AvenirNext,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}

@UnstableApi
@Composable
private fun MainControlButtons(
    coverPrimary: Color,
    isPlaying: Boolean,
    viewModel: MusicViewModel,
    isCompact: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { viewModel.playPrevious() },
            modifier = Modifier.size(if (isCompact) 44.dp else 48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_left),
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
            )
        }

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

        IconButton(
            onClick = { viewModel.playNext() },
            modifier = Modifier.size(if (isCompact) 44.dp else 48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_right),
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
            )
        }
    }
}

@UnstableApi
@Composable
private fun UtilityButtons(
    viewModel: MusicViewModel,
    isCompact: Boolean = false
) {

    val isRepeatEnabled by viewModel.isRepeatEnabled.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = viewModel::toggleShuffle,
            modifier = Modifier.size(if (isCompact) 44.dp else 48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shuffle),
                contentDescription = "Shuffle",
                tint = if (isShuffleEnabled) {
                    ZenifyPrimary
                } else {
                   Color.White.copy(alpha = 0.8f)
                },
                modifier = Modifier.size(if (isCompact) 20.dp else 22.dp)
            )
        }

        IconButton(
            onClick = viewModel::toggleRepeat,
            modifier = Modifier.size(if (isCompact) 44.dp else 48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_repeat),
                contentDescription = "Repeat",
                tint = if (isRepeatEnabled) {
                    ZenifyPrimary
                } else {
                   Color.White.copy(alpha = 0.8f)
                },
                modifier = Modifier.size(if (isCompact) 20.dp else 22.dp)
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier.size(if (isCompact) 44.dp else 48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share",
                tint =Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(if (isCompact) 20.dp else 22.dp)
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier.size(if (isCompact) 44.dp else 48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = "Bookmark",
                tint =Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(if (isCompact) 20.dp else 22.dp)
            )
        }
    }
}

@Preview(showBackground = true,/* device = "spec:width=360dp,height=640dp"*/)
@Composable
fun PlayerScreenPreview() {
    PlayerScreen(
        onMinimizeClick = {},
        viewModel = rememberFakeMusicViewModel()
    )
}