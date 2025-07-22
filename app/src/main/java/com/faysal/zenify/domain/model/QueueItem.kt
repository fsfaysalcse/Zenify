package com.faysal.zenify.domain.model

data class QueueItem(
    val id: String,
    val audioId: String,
    val audio: Audio,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis()
)