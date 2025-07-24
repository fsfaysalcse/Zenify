package com.faysal.zenify.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.faysal.zenify.R
import com.faysal.zenify.ui.components.ActionButton
import com.faysal.zenify.ui.components.AnimatedBackgroundCircles
import com.faysal.zenify.ui.components.PermissionCard
import com.faysal.zenify.ui.theme.ProductSans
import com.faysal.zenify.ui.theme.ZenifyPrimary

@Composable
fun OnBoardScreen(navController: NavHostController) {
    val context = LocalContext.current
    val permissions = remember { PermissionState(context) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissions.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissions.updateNotificationPermission(isGranted)
    }

    val musicLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissions.updateMusicPermission(isGranted)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(createAnimatedGradient())
    ) {
        AnimatedBackgroundCircles()

        OnBoardingContent(
            permissions = permissions,
            onRequestNotificationPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onRequestMusicPermission = {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                musicLauncher.launch(permission)
            },
            onContinue = { navController.navigate(Screen.Home.route) }
        )
    }
}


@Composable
private fun createAnimatedGradient(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF000000),
            Color(0xFF060612),
            Color(0xFF0C0C22),
            Color(0xFF121232),
        ),
        start = Offset(gradientOffset * 1000, 0f),
        end = Offset((1f - gradientOffset) * 1000, 1000f)
    )
}


@Composable
private fun OnBoardingContent(
    permissions: PermissionState,
    onRequestNotificationPermission: () -> Unit,
    onRequestMusicPermission: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AppHeader()

        PermissionsSection(
            permissions = permissions,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestMusicPermission = onRequestMusicPermission
        )

        ActionButton(
            enabled = permissions.allGranted,
            onClick = onContinue
        )
    }
}

@Composable
private fun AppHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "header")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(120.dp)
                .scale(pulseScale),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.zenify),
                    contentDescription = "Zenify Logo",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to Zenify",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = ProductSans,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Your personal music sanctuary",
            fontFamily = ProductSans,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PermissionsSection(
    permissions: PermissionState,
    onRequestNotificationPermission: () -> Unit,
    onRequestMusicPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PermissionCard(
            title = "Music Access",
            description = "Access your music library",
            icon = painterResource(id = R.drawable.ic_play),
            isGranted = permissions.musicGranted,
            onRequestPermission = onRequestMusicPermission,
            shouldOpenSettings = permissions.musicDeniedTooMuch
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionCard(
                title = "Notifications",
                description = "Playback via notification",
                icon = painterResource(id = R.drawable.ic_popup_reminder),
                isGranted = permissions.notificationGranted,
                onRequestPermission = onRequestNotificationPermission,
                shouldOpenSettings = permissions.notificationDeniedTooMuch
            )
        }
    }
}


private class PermissionState(private val context: Context) {
    var notificationGranted by mutableStateOf(checkNotificationPermission())
    var musicGranted by mutableStateOf(checkMusicPermission())

    private var notificationDeniedCount by mutableIntStateOf(0)
    private var musicDeniedCount by mutableIntStateOf(0)

    val notificationDeniedTooMuch get() = notificationDeniedCount >= 3
    val musicDeniedTooMuch get() = musicDeniedCount >= 3

    val allGranted: Boolean get() = notificationGranted && musicGranted

    fun updateNotificationPermission(granted: Boolean) {
        notificationGranted = granted
        if (!granted) notificationDeniedCount++
    }

    fun updateMusicPermission(granted: Boolean) {
        musicGranted = granted
        if (!granted) musicDeniedCount++
    }

    fun refresh() {
        notificationGranted = checkNotificationPermission()
        musicGranted = checkMusicPermission()
    }

    private fun checkNotificationPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

    private fun checkMusicPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
}


@Preview(showBackground = true)
@Composable
private fun OnBoardScreenPreview() {
    MaterialTheme {
        OnBoardScreen(navController = rememberNavController())
    }
}