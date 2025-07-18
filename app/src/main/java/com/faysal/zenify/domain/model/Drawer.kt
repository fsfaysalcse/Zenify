package com.faysal.zenify.domain.model

import com.faysal.zenify.R

data class Drawer(
    val id: Int,
    val name: String,
    val icon: Int
)


val DRAWER_ITEM = listOf(
    Drawer(
        id = 1,
        name = "Tracks",
        icon = R.drawable.ic_artist
    ),
    Drawer(
        id = 2,
        name = "Equalizer",
        icon = R.drawable.ic_equalizer
    ),
    Drawer(
        id = 3,
        name = "Settings",
        icon = R.drawable.ic_settings
    ),
    Drawer(
        id = 4,
        name = "About",
        icon = R.drawable.ic_developer
    )
)
