package com.faysal.zenify.domain.model


data class AlbumInfo(
    val uri: android.net.Uri?,
    val name: String,
    val artist: String,
    val trackCount: Int,
    val tracks: List<Audio>
)