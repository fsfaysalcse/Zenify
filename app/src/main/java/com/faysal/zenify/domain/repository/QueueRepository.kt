package com.faysal.zenify.domain.repository
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.QueueItem
import com.faysal.zenify.domain.model.FavouriteAudio
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun getQueueItems(): Flow<List<QueueItem>>
    suspend fun addToQueue(audio: Audio)
    suspend fun addToQueueNext(audio: Audio, currentIndex: Int)
    suspend fun addMultipleToQueue(audios: List<Audio>)
    suspend fun removeFromQueue(queueItemId: String)
    suspend fun moveQueueItem(fromPosition: Int, toPosition: Int, itemId: String)
    suspend fun clearQueue()
    suspend fun getQueueSize(): Int
}

