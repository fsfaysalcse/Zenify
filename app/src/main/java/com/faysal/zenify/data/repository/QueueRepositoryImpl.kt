package com.faysal.zenify.data.repository

import com.faysal.zenify.data.database.dao.FavouriteDao
import com.faysal.zenify.data.database.dao.QueueDao
import com.faysal.zenify.data.mappers.toDomainModel
import com.faysal.zenify.data.mappers.toFavouriteEntity
import com.faysal.zenify.data.mappers.toQueueItemEntity
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.QueueItem
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.repository.QueueRepository
import com.faysal.zenify.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QueueRepositoryImpl(
    private val queueDao: QueueDao
) : QueueRepository {

    override fun getQueueItems(): Flow<List<QueueItem>> {
        return queueDao.getAllQueueItems().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addToQueue(audio: Audio) {
        val maxPosition = queueDao.getMaxPosition() ?: -1
        val queueItem = audio.toQueueItemEntity(maxPosition + 1)
        queueDao.insertQueueItem(queueItem)
    }

    override suspend fun addToQueueNext(audio: Audio, currentIndex: Int) {
        val insertPosition = currentIndex + 1
        val currentItems = queueDao.getAllQueueItems()

        currentItems.collect { items ->
            items.filter { it.position >= insertPosition }
                .forEach { item ->
                    queueDao.updateItemPosition(item.id, item.position + 1)
                }
        }

        val queueItem = audio.toQueueItemEntity(insertPosition)
        queueDao.insertQueueItem(queueItem)
    }

    override suspend fun addMultipleToQueue(audios: List<Audio>) {
        val maxPosition = queueDao.getMaxPosition() ?: -1
        val queueItems = audios.mapIndexed { index, audio ->
            audio.toQueueItemEntity(maxPosition + 1 + index)
        }
        queueDao.insertQueueItems(queueItems)
    }

    override suspend fun removeFromQueue(queueItemId: String) {
        val item = queueDao.getQueueItemById(queueItemId)
        if (item != null) {
            queueDao.deleteQueueItemById(queueItemId)
            queueDao.updatePositionsAfterDeletion(item.position)
        }
    }

    override suspend fun moveQueueItem(fromPosition: Int, toPosition: Int, itemId: String) {
        queueDao.moveQueueItem(fromPosition, toPosition)
        queueDao.updateItemPosition(itemId, toPosition)
    }

    override suspend fun clearQueue() {
        queueDao.clearQueue()
    }

    override suspend fun getQueueSize(): Int {
        return queueDao.getQueueSize()
    }
}
