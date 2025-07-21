package com.faysal.zenify.ui.screen

sealed class Screen(val route: String) {
    object OnBoard : Screen("onboard")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object About : Screen("about")
}
