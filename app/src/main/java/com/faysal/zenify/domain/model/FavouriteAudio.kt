package com.faysal.zenify.domain.model

data class FavouriteAudio(
    val id: String,
    val audioId: String,
    val audio: Audio,
    val addedAt: Long = System.currentTimeMillis()
)
