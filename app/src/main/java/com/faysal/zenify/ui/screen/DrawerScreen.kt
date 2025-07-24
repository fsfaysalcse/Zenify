package com.faysal.zenify.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.faysal.zenify.domain.model.DRAWER_ITEM
import com.faysal.zenify.ui.components.DrawerItem
import com.faysal.zenify.ui.components.NavigationDrawerBackground

@Composable
fun DrawerScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    isDrawerOpen: Boolean = false
) {
    NavigationDrawerBackground {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                var selectedDrawerState by rememberSaveable { mutableIntStateOf(1) }
                val drawerItems = DRAWER_ITEM
                val drawerVisible = remember { mutableStateOf(false) }

                LaunchedEffect(isDrawerOpen) { drawerVisible.value = isDrawerOpen }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    drawerItems.forEach { drawer ->
                        DrawerItem(
                            isSelected = drawer.id == selectedDrawerState,
                            drawer = drawer,
                            onItemClick = {
                               // selectedDrawerState = drawer.id
                                when (drawer.id) {
                                    2 -> navController?.navigate(Screen.Favourite.route)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DrawerScreenPreview() {
    DrawerScreen(
        modifier = Modifier.fillMaxSize(),
        isDrawerOpen = true
    )
}
