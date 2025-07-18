package com.faysal.zenify.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.R
import com.faysal.zenify.domain.model.DRAWER_ITEM
import com.faysal.zenify.ui.theme.Nunito
import com.faysal.zenify.ui.theme.blackToDreamWave
import com.faysal.zenify.ui.components.DrawerItem
import com.faysal.zenify.ui.theme.ProductSans
import me.fsfaysalcse.discoverbd.ui.model.DrawerState

@Composable
fun DrawerScreen(
    modifier: Modifier = Modifier,
    isDrawerOpen : Boolean = false
) {

    //Gradient background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(blackToDreamWave)
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

        /*Text(
            text = "Zenify v1.0.0".uppercase(),
            color = Color.Black,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            lineHeight = 20.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomStart)
        )*/
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