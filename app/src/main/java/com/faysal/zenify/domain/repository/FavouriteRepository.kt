package com.faysal.zenify.domain.repository

import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.FavouriteAudio
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavourites(): Flow<List<FavouriteAudio>>
    suspend fun addToFavourites(audio: Audio)
    suspend fun removeFromFavourites(audioId: String)
    suspend fun isFavourite(audioId: String): Boolean
    fun isFavouriteFlow(audioId: String): Flow<Boolean>
    suspend fun toggleFavourite(audio: Audio)
    suspend fun clearFavourites()
    suspend fun getFavouritesCount(): Int
}