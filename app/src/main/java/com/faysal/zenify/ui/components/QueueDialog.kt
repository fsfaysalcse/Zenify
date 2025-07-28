package com.faysal.zenify.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.R
import com.faysal.zenify.ui.mock.rememberFakeQueueViewModel
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.viewModels.QueueViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QueueDialog(
    viewModel: QueueViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        QueueDialogContent(
            viewModel = viewModel,
            onDismiss = onDismiss
        )
    }
}

@UnstableApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QueueDialogContent(
    viewModel: QueueViewModel,
    onDismiss: () -> Unit
) {
    val queueItems by viewModel.queueItems.collectAsStateWithLifecycle()
    val currentAudio by viewModel.currentAudio.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val bitmapCache = remember { mutableMapOf<String, Bitmap?>() }
    val lazyListState = rememberLazyListState()
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        viewModel.moveQueueItem(from.index, to.index)
        ViewCompat.performHapticFeedback(
            view,
            AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS
        )
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            viewModel.clearError()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.85f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            QueueHeader(
                queueSize = queueItems.size,
                onDismiss = onDismiss,
                onPlayQueue = { viewModel.playQueue() },
                onShuffleQueue = { viewModel.shuffleQueue() },
                onClearQueue = { viewModel.clearQueue() }
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.isEmpty -> {
                    EmptyQueueContent()
                }

                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = queueItems,
                            key = { _, item -> item.id }
                        ) { index, queueItem ->
                            ReorderableItem(reorderableLazyListState, key = queueItem.id) { isDragging ->
                                val elevation by animateDpAsState(
                                    targetValue = if (isDragging) 8.dp else 2.dp,
                                    label = "elevation"
                                )

                                Surface {
                                    QueueAudioItem(
                                        queueItem = queueItem,
                                        bitmapCache = bitmapCache,
                                        isCurrentlyPlaying = currentAudio?.id == queueItem.audio.id,
                                        isPlaying = isPlaying,
                                        onItemClick = { viewModel.playFromQueue(queueItem) },
                                        onRemove = { viewModel.removeFromQueue(queueItem.id) },
                                        reorderableScope = this@ReorderableItem,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueHeader(
    queueSize: Int,
    onDismiss: () -> Unit,
    onPlayQueue: () -> Unit,
    onShuffleQueue: () -> Unit,
    onClearQueue: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "Queue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AvenirNext,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = onClearQueue) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = "Clear Queue",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (queueSize > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$queueSize songs",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = AvenirNext,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Row {
                    OutlinedButton(
                        onClick = onPlayQueue,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Play",
                            fontSize = 12.sp,
                            fontFamily = AvenirNext
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onShuffleQueue,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shuffle),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shuffle",
                            fontSize = 12.sp,
                            fontFamily = AvenirNext
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun EmptyQueueContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_queue_music),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Queue is empty",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = AvenirNext,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add songs to your queue to see them here",
                fontSize = 14.sp,
                fontFamily = AvenirNext,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@UnstableApi
@Preview
@Composable
private fun QueueDialogPreview() {
    QueueDialogContent(
        viewModel = rememberFakeQueueViewModel(),
        onDismiss = {}
    )
}