package com.faysal.zenify

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faysal.zenify.ui.screen.HomeScreen
import com.faysal.zenify.ui.screen.OnBoardScreen
import com.faysal.zenify.ui.screen.Screen
import com.faysal.zenify.ui.screen.SettingsScreen
import com.faysal.zenify.ui.theme.ZenifyTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )

        // Permission check
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true

        val hasMusicPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        val startDestination = if (hasNotificationPermission && hasMusicPermission) {
            Screen.Home.route
        } else {
            Screen.OnBoard.route
        }

        setContent {
            ZenifyTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Screen.OnBoard.route) {
                        OnBoardScreen(navController)
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(navController)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(navController)
                    }
                }
            }
        }
    }
}
