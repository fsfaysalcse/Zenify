package com.faysal.zenify.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.faysal.zenify.domain.repository.FakeAudioRepository
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.MusicViewModel
import com.faysal.zenify.ui.widgets.AudioItem
import com.faysal.zenify.ui.widgets.PlayerBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun MusicScreen(viewModel: MusicViewModel = koinViewModel()) {
    val context = LocalContext.current
    val audios by viewModel.audios.collectAsState()
    val currentAudio by viewModel.currentAudio.collectAsState()

    // Permissions
    val permissions = mutableListOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val audioPermissionGranted = permissionsResult[permissions[0]] == true
        val notificationPermissionGranted = if (permissions.size > 1) {
            permissionsResult[Manifest.permission.POST_NOTIFICATIONS] == true
        } else true
        if (audioPermissionGranted && notificationPermissionGranted) {
            viewModel.loadAudios(context)
        }
    }

    LaunchedEffect(Unit) {
        val audioPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED
        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
        if (audioPermissionGranted && notificationPermissionGranted) {
            viewModel.loadAudios(context)
        } else {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    var selectedFilter by remember { mutableStateOf("Track") }

    val filteredAudios by remember(selectedFilter, audios) {
        derivedStateOf {
            when (selectedFilter) {
                "Album" -> audios.sortedBy { it.album }
                "Artist" -> audios.sortedBy { it.artist }
                "Folder" -> audios.sortedBy { it.uri.pathSegments.dropLast(1).lastOrNull() ?: "" }
                else -> audios
            }
        }
    }

    val bitmapCache = remember { mutableStateMapOf<Long, Bitmap?>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Track", "Album", "Artist", "Folder").forEach { label ->
                    item {
                        val isSelected = selectedFilter == label
                        Text(
                            text = label,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { selectedFilter = label }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (audios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No audio files found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .weight(1f)
                ) {
                    items(filteredAudios) { audio ->
                        AudioItem(
                            audio = audio,
                            bitmapCache = bitmapCache,
                            onClick = { viewModel.playAudio(context, audio) }
                        )
                    }
                }
            }

            if (currentAudio != null) {
                PlayerBottomSheet(
                    audio = currentAudio!!,
                    bitmap = bitmapCache[currentAudio!!.id],
                    viewModel = viewModel
                )
            }
        }
    }
}





@Composable
fun rememberFakeMusicViewModel(): MusicViewModel {
    val context = LocalContext.current
    val repository = FakeAudioRepository()
    return remember {
        object : MusicViewModel(GetAudiosUseCase(repository)) {
            init {
                loadAudios(context)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MusicScreenPreview() {
    MusicScreen(viewModel = rememberFakeMusicViewModel())
}