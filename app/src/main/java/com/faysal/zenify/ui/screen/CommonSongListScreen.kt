package com.faysal.zenify.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.UiTypes
import com.faysal.zenify.ui.components.AudioItem
import com.faysal.zenify.ui.components.IconTextButton
import com.faysal.zenify.ui.components.MarqueeText
import com.faysal.zenify.ui.theme.AvenirNext
import com.faysal.zenify.ui.theme.MusicPrimaryColor
import com.faysal.zenify.ui.util.extractHeaderInfo
import com.faysal.zenify.ui.util.getEmbeddedCover
import com.faysal.zenify.ui.util.sampleAudios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun CommonSongListScreen(
    title : String,
    audios: List<Audio>,
    onBack: () -> Unit,
    uiTypes: UiTypes = UiTypes.Album,
    onPlayAll: (List<Audio>) -> Unit,
    onPlaySong: (Audio) -> Unit,
    bitmapCache: MutableMap<Long, Bitmap?>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
        }



        val firstItem = audios.firstOrNull()

        val context = LocalContext.current
        var isLoading by remember(firstItem?.id) { mutableStateOf(true) }

        LaunchedEffect(firstItem?.uri) {
            if (!bitmapCache.contains(firstItem?.id)) {
                isLoading = true
                val art = withContext(Dispatchers.IO) {
                    getEmbeddedCover(context, firstItem?.uri)
                }
                bitmapCache[firstItem!!.id] = art
                isLoading = false
            } else {
                isLoading = false
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art or Placeholder
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MusicPrimaryColor)
            ) {
                when {
                    bitmapCache[firstItem?.id] != null -> {
                        Image(
                            bitmap = bitmapCache[firstItem?.id]!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    isLoading -> {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(28.dp)
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.default_cover),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            val headerInfo = extractHeaderInfo(title, audios, uiTypes)

            // Title & Artist
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = headerInfo.hint,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.White,
                    fontFamily = AvenirNext,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.White,
                    fontFamily = AvenirNext,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))


                MarqueeText(
                    text = headerInfo.subTitle,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = AvenirNext,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )


            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTextButton(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.ic_play),
                text = "Play",
                onClick = {  onPlayAll(audios) },
                backgroundColor = Color(0xFF000000),
                contentColor = Color.White
            )

            IconTextButton(
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.ic_shuffle),
                text = "Shuffle",
                onClick = { },
                backgroundColor = Color(0xFFF2F3F5),
                contentColor = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(audios) { audio ->
                AudioItem(
                    audio = audio,
                    bitmapCache = bitmapCache,
                    onClick = { onPlaySong(audio) }
                )

                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}
/*
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CommonSongListScreenPreview() {
    CommonSongListScreen(
        title = "Album A",
        audios = sampleAudios,
        onBack = {},
        onPlayAll = {},
        onPlaySong = {},
        bitmapCache = mutableMapOf()
    )
}*/
