package com.faysal.zenify.ui.widgets


import com.faysal.zenify.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.ui.theme.AvenirNext

/**
 * A top bar for the lyrics section with a title and action buttons.
 *
 * @param onShareClick Callback triggered when the share button is clicked.
 * @param onFullScreenClick Callback triggered when the fullscreen button is clicked.
 */


@Composable
fun LyricsHeaderBar(
    onShareClick: () -> Unit = {},
    onFullScreenClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Lyrics",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AvenirNext,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "Share",
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onFullScreenClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_full_screen),
                    contentDescription = "Full Screen",
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun LyricsHeaderBarPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1A1A2E)
        ) {
            LyricsHeaderBar(
                onShareClick = {},
                onFullScreenClick = {}
            )
        }
    }
}
