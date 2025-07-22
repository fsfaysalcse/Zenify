package com.faysal.zenify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysal.zenify.domain.model.DRAWER_ITEM
import com.faysal.zenify.domain.model.Drawer
import com.faysal.zenify.ui.theme.NavFont

@Composable
fun DrawerItem(
    isSelected: Boolean,
    drawer: Drawer,
    onItemClick: () -> Unit
) {

    val selectedColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(0.4f)
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick()
            }
            .padding(vertical = 5.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

       /* Icon(
            painter = painterResource( drawer.icon),
            contentDescription = null,
            tint = selectedColor,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(30.dp)
                .align(Alignment.CenterVertically),
        )*/

        Text(
            text = drawer.name,
            color = selectedColor,
            fontSize = 30.sp,
            letterSpacing = 3.sp,
            fontFamily = NavFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 13.dp)
                .align(Alignment.CenterVertically)
        )
    }
}


/*
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun DrawerItemPreview() {
    DrawerItem(
        isSelected = true,
        drawer = DRAWER_ITEM.first(),
        onItemClick = {}
    )
}*/
