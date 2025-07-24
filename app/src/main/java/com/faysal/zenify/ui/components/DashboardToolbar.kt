package com.faysal.zenify.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.faysal.zenify.R
import me.fsfaysalcse.discoverbd.ui.model.DrawerState

@Composable
fun DashboardToolbar(
    drawerState: DrawerState, onNavigationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentHeight()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { onNavigationClick() }) {
            Crossfade(
                targetState = drawerState,
                animationSpec = tween(500, 0, easing = LinearOutSlowInEasing)
            ) { state ->
                val icon = if (state == DrawerState.CLOSED) {
                    R.drawable.ic_navigation
                } else {
                    R.drawable.ic_open_navigation
                }

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp, 40.dp),
                    tint = Color.White
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.bg_title_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(80.dp, 40.dp)
                .padding(start = 16.dp),
        )
    }
}

@Preview
@Composable
fun DashboardToolbarPreview() {
    DashboardToolbar(
        drawerState = DrawerState.CLOSED,
        onNavigationClick = {}
    )
}