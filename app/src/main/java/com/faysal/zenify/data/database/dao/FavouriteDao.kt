package com.faysal.zenify.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.faysal.zenify.data.database.entity.FavouriteAudioEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourite_audios ORDER BY addedAt DESC")
    fun getAllFavourites(): Flow<List<FavouriteAudioEntity>>

    @Query("SELECT * FROM favourite_audios WHERE audioId = :audioId LIMIT 1")
    suspend fun getFavouriteByAudioId(audioId: String): FavouriteAudioEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_audios WHERE audioId = :audioId)")
    suspend fun isFavourite(audioId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_audios WHERE audioId = :audioId)")
    fun isFavouriteFlow(audioId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteAudioEntity)

    @Delete
    suspend fun deleteFavourite(favourite: FavouriteAudioEntity)

    @Query("DELETE FROM favourite_audios WHERE audioId = :audioId")
    suspend fun deleteFavouriteByAudioId(audioId: String)

    @Query("DELETE FROM favourite_audios")
    suspend fun clearFavourites()

    @Query("SELECT COUNT(*) FROM favourite_audios")
    suspend fun getFavouritesCount(): Int
}