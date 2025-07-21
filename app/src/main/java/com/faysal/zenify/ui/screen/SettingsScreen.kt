package com.faysal.zenify.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(navController: NavHostController) {
    Box {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Back")
        }
    }
}