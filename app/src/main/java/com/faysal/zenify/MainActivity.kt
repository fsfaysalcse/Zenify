package com.faysal.zenify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.faysal.zenify.ui.screen.PlayerScreen
import com.faysal.zenify.ui.theme.ZenifyTheme
import com.faysal.zenify.ui.widgets.GestureMusicButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenifyTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    /*var isPlaying by remember { mutableStateOf(false) }

                    GestureMusicButton(
                        isPlaying = isPlaying,
                        onPlayPause = { isPlaying = it },
                        onLongPress = { *//* Handle long press *//* },
                        onSwipe = { direction -> println("Swiped: $direction") },
                        onHold = { direction -> println("Held: $direction") }
                    )*/

                    PlayerScreen()
                }

            }
        }
    }
}
