package com.faysal.zenify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.faysal.zenify.ui.screen.HomeScreen
import com.faysal.zenify.ui.theme.ZenifyTheme
import com.faysal.zenify.ui.theme.blackToDreamWave
import com.faysal.zenify.ui.theme.blackToNeveBlue

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.Transparent.toArgb(),
            )
        )

        setContent {
            ZenifyTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(blackToDreamWave)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

