package com.faysal.zenify.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SerializableAudio(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: String,
    val duration: Long,
    val year: String = ""
)