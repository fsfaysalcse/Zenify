package com.faysal.zenify.ui.widgets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.faysal.zenify.R
import com.faysal.zenify.service.MusicService
import com.faysal.zenify.ui.MusicViewModel
import com.faysal.zenify.ui.model.Audio
import kotlinx.coroutines.delay

@Composable
fun PlayerBottomSheet(
    audio: Audio,
    bitmap: Bitmap?,
    viewModel: MusicViewModel
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var musicService by remember { mutableStateOf<MusicService?>(null) }

    // Service connection for MusicService
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as MusicService.MusicBinder
                musicService = binder.getService()
                isPlaying = musicService?.isPlaying() ?: false
            }

            override fun onServiceDisconnected(name: ComponentName) {
                musicService = null
            }
        }
    }

    // Bind to MusicService
    DisposableEffect(audio) {
        val serviceIntent = Intent(context, MusicService::class.java)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    // Update progress
    LaunchedEffect(isPlaying, musicService) {
        while (isPlaying && musicService != null) {
            musicService?.player?.let { player ->
                val currentPosition = player.currentPosition
                val duration = player.duration
                progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
            }
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.default_cover),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        audio.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        audio.artist,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Image(
                    painter = painterResource(
                        id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            if (isPlaying) {
                                viewModel.pauseAudio()
                                isPlaying = false
                            } else {
                                viewModel.resumeAudio()
                                isPlaying = true
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}