package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.faysal.zenify.domain.model.Audio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderListScreen(
    audios: List<Audio>,
    onFolderClick: (String) -> Unit,
    onBack: () -> Unit,
    bitmapCache: MutableMap<Long, Bitmap?>
) {
    val folders = audios.map { it.uri.pathSegments.dropLast(1).lastOrNull() ?: "Unknown" }
        .distinct()
        .sorted()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text("Folders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                items(folders) { folder ->
                    ListItem(
                        headlineContent = { Text(folder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFolderClick(folder) }
                            .padding(vertical = 4.dp)
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}
