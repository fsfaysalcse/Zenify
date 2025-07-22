package com.faysal.zenify.data.database


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.faysal.zenify.data.database.dao.FavouriteDao
import com.faysal.zenify.data.database.dao.QueueDao
import com.faysal.zenify.data.database.entity.FavouriteAudioEntity
import com.faysal.zenify.data.database.entity.QueueItemEntity

@Database(
    entities = [QueueItemEntity::class, FavouriteAudioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ZenifyDatabase : RoomDatabase() {

    abstract fun queueDao(): QueueDao
    abstract fun favouriteDao(): FavouriteDao

    companion object {
        const val DATABASE_NAME = "zenify_database"

        fun create(context: Context): ZenifyDatabase {
            return Room.databaseBuilder(
                context,
                ZenifyDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}