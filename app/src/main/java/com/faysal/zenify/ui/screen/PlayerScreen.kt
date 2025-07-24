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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.ui.components.AIMusicPlayerBackground
import com.faysal.zenify.ui.components.GestureMusicButton
import com.faysal.zenify.ui.components.LyricsHeaderBar
import com.faysal.zenify.ui.components.RhythmTimeline
import com.faysal.zenify.ui.mock.rememberFakeMusicViewModel
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
    val density = LocalDensity.current

    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    val musicCover = getEmbeddedCover(context = context, uri = currentAudio?.uri)
    val imageBitmap = musicCover?.asImageBitmap()

    var imageColors by remember { mutableStateOf(ImageColors()) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(imageBitmap, currentAudio?.uri) {
        imageColors = when {
            imageBitmap != null -> extractColorsFromImage(imageBitmap)
            currentAudio?.uri != null -> extractColorsFromUri(context, currentAudio!!.uri.toString())
            else -> ImageColors()
        }
    }


    AIMusicPlayerBackground(
        primaryColor = imageColors.vibrant,
        secondaryColor = imageColors.vibrant,
        accentColor = imageColors.vibrant
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { screenSize = it }
        ) {

            val availableHeight = with(density) { screenSize.height.toDp() }
            val availableWidth = with(density) { screenSize.width.toDp() }

            PlayerLayout(
                onMinimizeClick = onMinimizeClick,
                imageBitmap = imageBitmap,
                currentAudioId = currentAudio?.id ?: "",
                currentAudioTitle = currentAudio?.title ?: "Unknown",
                currentAudioArtist = currentAudio?.artist ?: "Unknown",
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                imageColors = imageColors,
                viewModel = viewModel,
                availableHeight = availableHeight,
                availableWidth = availableWidth
            )
        }
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
private fun PlayerLayout(
    onMinimizeClick: () -> Unit,
    imageBitmap: ImageBitmap?,
    currentAudioTitle: String,
    currentAudioArtist: String,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    imageColors: ImageColors,
    viewModel: MusicViewModel,
    availableHeight: Dp,
    availableWidth: Dp,
    currentAudioId: String
) {
    val headerHeight = 64.dp
    val contentHeight = availableHeight - headerHeight
    val albumArtSize = kotlin.math.min(contentHeight.value * 0.4f, availableWidth.value * 0.75f).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        PlayerHeader(
            onMinimizeClick = onMinimizeClick,
            modifier = Modifier.statusBarsPadding()
        )

        PlayerContent(
            imageBitmap = imageBitmap,
            currentAudioId = currentAudioId,
            currentAudioTitle = currentAudioTitle,
            currentAudioArtist = currentAudioArtist,
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            imageColors = imageColors,
            viewModel = viewModel,
            albumArtSize = albumArtSize,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PlayerHeader(
    onMinimizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(64.dp),
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
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Now Playing",
                color = MaterialTheme.colorScheme.onSurface,
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
                    tint = MaterialTheme.colorScheme.onSurface,
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
    albumArtSize: Dp,
    modifier: Modifier = Modifier,
    currentAudioId: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AlbumArtSection(
            imageBitmap = imageBitmap,
            size = albumArtSize
        )

        TrackInfoSection(
            title = currentAudioTitle,
            artist = currentAudioArtist
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RhythmTimeline(
                modifier = Modifier.fillMaxWidth(),
                currentPositionMs = currentPosition,
                durationMs = duration,
                onSeek = viewModel::seekTo
            )

            MainControlButtons(
                coverPrimary = imageColors.vibrant,
                isPlaying = isPlaying,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(8.dp))

            UtilityButtons(
                viewModel = viewModel,
                imageColors = imageColors
            )
        }

        LyricsHeaderBar(
            onShareClick = {},
            onFullScreenClick = {},
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun AlbumArtSection(
    imageBitmap: ImageBitmap?,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
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
                    .size(size)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black.copy(alpha = 0.3f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
            )
        } else {
            Surface(
                modifier = Modifier
                    .size(size)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.default_cover),
                    contentDescription = "No Cover",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
private fun TrackInfoSection(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 22.sp,
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontSize = 16.sp,
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { viewModel.playPrevious() },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_left),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
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
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_right),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@UnstableApi
@Composable
private fun UtilityButtons(
    viewModel: MusicViewModel,
    imageColors: ImageColors = ImageColors(),
    modifier: Modifier = Modifier,
) {
    val currentAudio = viewModel.currentAudio.collectAsState().value
    val isRepeatEnabled by viewModel.isRepeatEnabled.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()

    val isFavourite by viewModel.isFavourite(currentAudio?.id ?: "").collectAsState(initial = false)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = viewModel::toggleShuffle,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = imageColors.muted.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shuffle),
                contentDescription = "Shuffle",
                tint = if (isShuffleEnabled) {
                    ZenifyPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                },
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = viewModel::toggleRepeat,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = imageColors.muted.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_repeat),
                contentDescription = "Repeat",
                tint = if (isRepeatEnabled) {
                    ZenifyPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                },
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = imageColors.muted.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = {
                if (isFavourite) {
                    currentAudio?.let {
                        viewModel.removeFromFavourites(it.id)
                    }
                } else {
                    currentAudio?.let {
                        viewModel.toggleFavourite(it)
                    }
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = imageColors.muted.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            val icon = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder

            Icon(
                imageVector = icon,
                contentDescription = "Bookmark",
                tint = if (isFavourite) {
                    ZenifyPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                },
                modifier = Modifier.size(22.dp)
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