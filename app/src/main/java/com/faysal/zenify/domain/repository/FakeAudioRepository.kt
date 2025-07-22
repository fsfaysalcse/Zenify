package com.faysal.zenify.domain.repository
import android.graphics.Bitmap
import android.net.Uri
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.model.QueueItem
import com.faysal.zenify.ui.util.sampleAudios
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAudioRepository : AudioRepository {

    override fun getAllAudio(): List<Audio> {
        return sampleAudios
    }

    override suspend fun getSongCover(uri: Uri): Bitmap? {
       return null
    }
}

class FakeQueueRepository : QueueRepository {

    private val queueItems = mutableListOf<Audio>()

    override fun getQueueItems(): Flow<List<QueueItem>> {
       return flowOf(emptyList())
    }


    override suspend fun addToQueue(audio: Audio) {
        queueItems.add(audio)
    }

    override suspend fun addToQueueNext(audio: Audio, currentIndex: Int) {
        queueItems.add(currentIndex + 1, audio)
    }

    override suspend fun addMultipleToQueue(audios: List<Audio>) {
        queueItems.addAll(audios)
    }

    override suspend fun removeFromQueue(queueItemId: String) {
        queueItems.removeIf { it.id == queueItemId }
    }

    override suspend fun moveQueueItem(fromPosition: Int, toPosition: Int, itemId: String) {
        val item = queueItems.removeAt(fromPosition)
        queueItems.add(toPosition, item)
    }

    override suspend fun clearQueue() {
        queueItems.clear()
    }

    override suspend fun getQueueSize(): Int {
        return queueItems.size
    }
}

class FakeFavouriteRepository : FavouriteRepository {

    private val favourites = mutableListOf<Audio>()

    override fun getFavourites(): Flow<List<FavouriteAudio>> {
        return flowOf(emptyList())
    }


    override suspend fun addToFavourites(audio: Audio) {
        if (!favourites.any { it.id == audio.id }) {
            favourites.add(audio)
        }
    }

    override suspend fun removeFromFavourites(audioId: String) {
        favourites.removeIf { it.id == audioId }
    }

    override suspend fun isFavourite(audioId: String): Boolean {
        return favourites.any { it.id == audioId }
    }

    override fun isFavouriteFlow(audioId: String): Flow<Boolean> {
        return flowOf(false)
    }

    override suspend fun toggleFavourite(audio: Audio) {
        if (isFavourite(audio.id)) {
            removeFromFavourites(audio.id)
        } else {
            addToFavourites(audio)
        }
    }

    override suspend fun clearFavourites() {
        favourites.clear()
    }

    override suspend fun getFavouritesCount(): Int {
        return favourites.size
    }
}
