package com.faysal.zenify.data.mappers

import androidx.core.net.toUri
import com.faysal.zenify.data.database.entity.FavouriteAudioEntity
import com.faysal.zenify.data.database.entity.QueueItemEntity
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.model.QueueItem


fun QueueItemEntity.toDomainModel(): QueueItem {
    return QueueItem(
        id = id,
        audioId = audioId,
        audio = Audio(
            id = audioId,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            uri = uri.toUri(),
            year = ""
        ),
        position = position,
        addedAt = addedAt
    )
}

fun QueueItem.toEntity(): QueueItemEntity {
    return QueueItemEntity(
        id = id,
        audioId = audioId,
        title = audio.title,
        artist = audio.artist,
        album = audio.album,
        duration = audio.duration,
        uri = audio.uri.toString(),
        position = position,
        addedAt = addedAt
    )
}

fun Audio.toQueueItemEntity(position: Int): QueueItemEntity {
    return QueueItemEntity(
        id = "${System.currentTimeMillis()}_$id",
        audioId = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = uri.toString(),
        position = position,
        addedAt = System.currentTimeMillis()
    )
}

fun FavouriteAudioEntity.toDomainModel(): FavouriteAudio {
    return FavouriteAudio(
        id = id,
        audioId = audioId,
        audio = Audio(
            id = audioId,
            title = title,
            artist = artist,
            album = album,
            duration = duration,
            uri = uri.toUri(),
            year = ""
        ),
        addedAt = addedAt
    )
}

fun FavouriteAudio.toEntity(): FavouriteAudioEntity {
    return FavouriteAudioEntity(
        id = id,
        audioId = audioId,
        title = audio.title,
        artist = audio.artist,
        album = audio.album,
        duration = audio.duration,
        uri = audio.uri.toString(),
        addedAt = addedAt
    )
}

fun Audio.toFavouriteEntity(): FavouriteAudioEntity {
    return FavouriteAudioEntity(
        id = "${System.currentTimeMillis()}_$id",
        audioId = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = uri.toString(),
        addedAt = System.currentTimeMillis()
    )
}