package com.faysal.zenify.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R
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
    onFullScreenClick: () -> Unit = {},
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Up Next",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AvenirNext,
                modifier = Modifier
            )
            Text(
                text = "Life without you",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = AvenirNext,
                modifier = Modifier
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.Transparent.copy(alpha = 0.3f), shape = CircleShape)
                .clickable { onFullScreenClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_up),
                contentDescription = "Share",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(16.dp)
            )
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
                onFullScreenClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
