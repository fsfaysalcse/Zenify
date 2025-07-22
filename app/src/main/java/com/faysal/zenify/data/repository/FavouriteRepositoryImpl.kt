package com.faysal.zenify.data.repository

import com.faysal.zenify.data.database.dao.FavouriteDao
import com.faysal.zenify.data.mappers.toDomainModel
import com.faysal.zenify.data.mappers.toFavouriteEntity
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FavouriteRepositoryImpl(
    private val favouriteDao: FavouriteDao
) : FavouriteRepository {

    override fun getFavourites(): Flow<List<FavouriteAudio>> {
        return favouriteDao.getAllFavourites().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addToFavourites(audio: Audio) {
        val favourite = audio.toFavouriteEntity()
        favouriteDao.insertFavourite(favourite)
    }

    override suspend fun removeFromFavourites(audioId: String) {
        favouriteDao.deleteFavouriteByAudioId(audioId)
    }

    override suspend fun isFavourite(audioId: String): Boolean {
        return favouriteDao.isFavourite(audioId)
    }

    override fun isFavouriteFlow(audioId: String): Flow<Boolean> {
        return favouriteDao.isFavouriteFlow(audioId)
    }

    override suspend fun toggleFavourite(audio: Audio) {
        if (isFavourite(audio.id)) {
            removeFromFavourites(audio.id)
        } else {
            addToFavourites(audio)
        }
    }

    override suspend fun clearFavourites() {
        favouriteDao.clearFavourites()
    }

    override suspend fun getFavouritesCount(): Int {
        return favouriteDao.getFavouritesCount()
    }
}