package com.faysal.zenify.ui.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.domain.model.BlindingLights
import com.faysal.zenify.domain.model.LyricLine
import com.faysal.zenify.domain.model.Song
import com.faysal.zenify.ui.theme.AvenirNext

/**
 * A composable widget to display song lyrics with real-time highlighting and auto-scroll.
 *
 * @param song The Song data containing title, artist, and timed lyrics.
 * @param currentTimeMs The current playback time in milliseconds.
 * @param isPlaying Whether the song is currently playing.
 * @param modifier Modifier for styling and layout adjustments.
 */
@Composable
fun LyricsCaption(
    song: Song,
    currentTimeMs: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var currentLyricIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentTimeMs) {
        val newIndex = song.lyrics.indexOfFirst { lyric ->
            currentTimeMs >= lyric.startTime && currentTimeMs < lyric.endTime
        }
        if (newIndex != -1 && newIndex != currentLyricIndex) {
            currentLyricIndex = newIndex
            listState.animateScrollToItem(maxOf(0, currentLyricIndex - 2))
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(song.lyrics) { index, lyric ->
            LyricLineItem(
                lyric = lyric,
                isCurrentLine = index == currentLyricIndex,
                isPlaying = isPlaying,
                currentTimeMs = currentTimeMs
            )
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun LyricLineItem(
    lyric: LyricLine,
    isCurrentLine: Boolean,
    isPlaying: Boolean,
    currentTimeMs: Long
) {
    val textColor by animateColorAsState(
        targetValue = if (isCurrentLine) Color.White else Color.Gray.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 300),
        label = "lyric_color"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = lyric.text,
            color =  textColor,
            fontSize = if (isCurrentLine) 20.sp else 18.sp,
            fontWeight = if (isCurrentLine) FontWeight.SemiBold else FontWeight.Medium,
            lineHeight = if (isCurrentLine) 28.sp else 26.sp,
            textAlign = TextAlign.Start,
            fontFamily = AvenirNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun LyricsCaptionPreview() {


    LyricsCaption(
        song = BlindingLights,
        currentTimeMs = 4700,
        isPlaying = true,
        modifier = Modifier.fillMaxSize()
    )
}