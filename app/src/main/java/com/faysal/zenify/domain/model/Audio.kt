package com.faysal.zenify.domain.model

import android.net.Uri

data class Audio(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri
)
