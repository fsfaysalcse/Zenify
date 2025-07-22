package com.faysal.zenify.domain.usecases

import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.QueueItem
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.repository.QueueRepository
import com.faysal.zenify.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow

class GetQueueItemsUseCase(private val repository: QueueRepository) {
    operator fun invoke(): Flow<List<QueueItem>> = repository.getQueueItems()
}

class AddToQueueUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke(audio: Audio) = repository.addToQueue(audio)
}

class AddToQueueNextUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke(audio: Audio, currentIndex: Int) =
        repository.addToQueueNext(audio, currentIndex)
}

class AddMultipleToQueueUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke(audios: List<Audio>) = repository.addMultipleToQueue(audios)
}

class RemoveFromQueueUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke(queueItemId: String) = repository.removeFromQueue(queueItemId)
}

class MoveQueueItemUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke(fromPosition: Int, toPosition: Int, itemId: String) =
        repository.moveQueueItem(fromPosition, toPosition, itemId)
}

class ClearQueueUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke() = repository.clearQueue()
}

class GetQueueSizeUseCase(private val repository: QueueRepository) {
    suspend operator fun invoke(): Int = repository.getQueueSize()
}