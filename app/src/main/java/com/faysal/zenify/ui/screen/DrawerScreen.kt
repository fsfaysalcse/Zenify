package com.faysal.zenify.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.faysal.zenify.domain.model.DRAWER_ITEM
import com.faysal.zenify.ui.components.DrawerItem
import com.faysal.zenify.ui.components.NavigationDrawerBackground

@Composable
fun DrawerScreen(
    modifier: Modifier = Modifier,
    isDrawerOpen : Boolean = false
) {

    //Gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            var selectedDrawerState by rememberSaveable {
                mutableIntStateOf(1)
            }
            val drawerItems = DRAWER_ITEM
            val drawerVisible = remember { mutableStateOf(false) }

            LaunchedEffect(isDrawerOpen) {
                drawerVisible.value = isDrawerOpen
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                drawerItems.forEach { drawer ->
                    val isSelected = drawer.id == selectedDrawerState

                    DrawerItem(
                        isSelected = isSelected,
                        drawer = drawer,
                        onItemClick = {
                            // handle click
                            selectedDrawerState = drawer.id
                        }
                    )
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