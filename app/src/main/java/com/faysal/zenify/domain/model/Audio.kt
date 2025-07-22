package com.faysal.zenify.domain.model

import android.net.Uri

data class Audio(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val year: String,
    val uri: Uri
)
