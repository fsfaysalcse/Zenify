package com.faysal.zenify.data.database.dao

import androidx.room.*
import com.faysal.zenify.data.database.entity.QueueItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {

    @Query("SELECT * FROM queue_items ORDER BY position ASC")
    fun getAllQueueItems(): Flow<List<QueueItemEntity>>

    @Query("SELECT * FROM queue_items WHERE id = :id")
    suspend fun getQueueItemById(id: String): QueueItemEntity?

    @Query("SELECT COUNT(*) FROM queue_items")
    suspend fun getQueueSize(): Int

    @Query("SELECT MAX(position) FROM queue_items")
    suspend fun getMaxPosition(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItem(item: QueueItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItems(items: List<QueueItemEntity>)

    @Delete
    suspend fun deleteQueueItem(item: QueueItemEntity)

    @Query("DELETE FROM queue_items WHERE id = :id")
    suspend fun deleteQueueItemById(id: String)

    @Query("DELETE FROM queue_items")
    suspend fun clearQueue()

    @Query("UPDATE queue_items SET position = position - 1 WHERE position > :deletedPosition")
    suspend fun updatePositionsAfterDeletion(deletedPosition: Int)

    @Query("UPDATE queue_items SET position = :newPosition WHERE id = :id")
    suspend fun updateItemPosition(id: String, newPosition: Int)

    @Transaction
    suspend fun moveQueueItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (pos in fromPosition + 1..toPosition) {
                updatePositionByPosition(pos, pos - 1)
            }
        } else {
            for (pos in toPosition until fromPosition) {
                updatePositionByPosition(pos, pos + 1)
            }
        }
    }

    @Query("UPDATE queue_items SET position = :newPosition WHERE position = :oldPosition")
    suspend fun updatePositionByPosition(oldPosition: Int, newPosition: Int)
}
