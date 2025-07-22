package com.faysal.zenify.domain.usecases

import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow


class GetFavouritesUseCase(private val repository: FavouriteRepository) {
    operator fun invoke(): Flow<List<FavouriteAudio>> = repository.getFavourites()
}

class AddToFavouritesUseCase(private val repository: FavouriteRepository) {
    suspend operator fun invoke(audio: Audio) = repository.addToFavourites(audio)
}

class RemoveFromFavouritesUseCase(private val repository: FavouriteRepository) {
    suspend operator fun invoke(audioId: String) = repository.removeFromFavourites(audioId)
}

class IsFavouriteUseCase(private val repository: FavouriteRepository) {
    suspend operator fun invoke(audioId: String): Boolean = repository.isFavourite(audioId)
}

class IsFavouriteFlowUseCase(private val repository: FavouriteRepository) {
    operator fun invoke(audioId: String): Flow<Boolean> = repository.isFavouriteFlow(audioId)
}

class ToggleFavouriteUseCase(private val repository: FavouriteRepository) {
    suspend operator fun invoke(audio: Audio) = repository.toggleFavourite(audio)
}

class ClearFavouritesUseCase(private val repository: FavouriteRepository) {
    suspend operator fun invoke() = repository.clearFavourites()
}

class GetFavouritesCountUseCase(private val repository: FavouriteRepository) {
    suspend operator fun invoke(): Int = repository.getFavouritesCount()
}