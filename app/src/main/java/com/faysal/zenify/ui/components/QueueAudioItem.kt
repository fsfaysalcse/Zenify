package com.faysal.zenify.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.QueueItem
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.util.getEmbeddedCover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sh.calvin.reorderable.ReorderableCollectionItemScope
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("WrongConstant")
@Composable
fun QueueAudioItem(
    queueItem: QueueItem,
    bitmapCache: MutableMap<String, Bitmap?>,
    isCurrentlyPlaying: Boolean,
    isPlaying: Boolean,
    onItemClick: () -> Unit,
    onRemove: () -> Unit,
    reorderableScope: ReorderableCollectionItemScope,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val haptic = LocalHapticFeedback.current

    var isLoading by remember(queueItem.audio.id) { mutableStateOf(true) }

    val offsetX = remember { Animatable(0f) }
    val swipeThreshold = with(density) { 120.dp.toPx() }
    var isRemoving by remember { mutableStateOf(false) }

    LaunchedEffect(queueItem.audio.uri) {
        if (!bitmapCache.contains(queueItem.audio.id)) {
            isLoading = true
            val art = withContext(Dispatchers.IO) {
                getEmbeddedCover(context, queueItem.audio.uri)
            }
            bitmapCache[queueItem.audio.id] = art
            isLoading = false
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(queueItem.id) {
        offsetX.snapTo(0f)
        isRemoving = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remove",
                    color = MaterialTheme.colorScheme.onError,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = AvenirNext
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (abs(offsetX.value) > swipeThreshold) {
                                    isRemoving = true
                                    offsetX.animateTo(
                                        targetValue = -size.width.toFloat(),
                                        animationSpec = tween(300)
                                    )
                                    onRemove()
                                } else {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(300)
                                    )
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        scope.launch {
                            val newOffset = (offsetX.value + dragAmount).coerceAtMost(0f)
                            offsetX.snapTo(newOffset)
                        }
                    }
                }
                .graphicsLayer {
                    alpha = if (isRemoving) 0.7f else 1f
                }
                .zIndex(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentlyPlaying) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onItemClick() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                with(reorderableScope) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drag_handle),
                        contentDescription = "Drag to reorder",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(20.dp)
                            .draggableHandle(
                                onDragStarted = {
                                    ViewCompat.performHapticFeedback(
                                        view,
                                        AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS
                                    )
                                },
                                onDragStopped = {
                                    ViewCompat.performHapticFeedback(
                                        view,
                                        AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS
                                    )
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    when {
                        bitmapCache[queueItem.audio.id] != null -> {
                            Image(
                                bitmap = bitmapCache[queueItem.audio.id]!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.default_cover),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    if (isCurrentlyPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                                ),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = queueItem.audio.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        fontFamily = AvenirNext,
                        color = if (isCurrentlyPlaying) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = queueItem.audio.artist,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontFamily = AvenirNext,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${queueItem.position + 1}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontFamily = AvenirNext
                )
            }
        }
    }
}

