package com.faysal.zenify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.theme.MusicPrimaryColor
import com.faysal.zenify.ui.theme.MusicSecondaryColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicBottomSheet(
    onDismissRequest: () -> Unit,
    onPlayPauseClick: () -> Unit,
    sheetState: SheetState,
    isPlaying: Boolean,
    currentAudio: Audio?
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MusicPrimaryColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    ) {
        SheetContent(
            isPlaying,
            currentAudio,
            onPlayPauseClick
        )
    }
}

@Composable
fun SheetContent(
    isPlaying: Boolean,
    currentAudio: Audio?,
    onPlayPauseClick: () -> Unit
) {



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MusicSecondaryColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable {
//                    coroutineScope.launch {
//                        sheetState.expand()
//                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentAudio?.title ?: "No Track",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = currentAudio?.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MusicBottomSheetPreview() {

    SheetContent(
        onPlayPauseClick = {},
        isPlaying = true,
        currentAudio = Audio(
            id = 1L,
            title = "Sample Track",
            artist = "Sample Artist",
            album = "Sample Album",
            duration = 240000L,
            uri = "/sample/path/to/track.mp3".toUri(),
        )
    )
}
