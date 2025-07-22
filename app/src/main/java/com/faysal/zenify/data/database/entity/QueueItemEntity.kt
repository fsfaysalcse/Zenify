package com.faysal.zenify.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "queue_items")
data class QueueItemEntity(
    @PrimaryKey
    val id: String,
    val audioId: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val position: Int,
    val addedAt: Long
)