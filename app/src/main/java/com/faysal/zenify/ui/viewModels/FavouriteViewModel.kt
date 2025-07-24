package com.faysal.zenify.ui.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.FavouriteAudio
import com.faysal.zenify.domain.usecases.*
import com.faysal.zenify.ui.util.sampleAudios
import com.faysal.zenify.ui.util.toFavAudio
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@UnstableApi
class FavouriteViewModel(
    private val serviceConnection: MusicServiceConnection,
    private val getFavouritesUseCase: GetFavouritesUseCase,
    private val addToFavouritesUseCase: AddToFavouritesUseCase,
    private val removeFromFavouritesUseCase: RemoveFromFavouritesUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val isFavouriteFlowUseCase: IsFavouriteFlowUseCase,
    private val addToQueueUseCase: AddToQueueUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouriteUiState())
    val uiState: StateFlow<FavouriteUiState> = _uiState.asStateFlow()

    var favourites: StateFlow<List<FavouriteAudio>> = getFavouritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentAudio: StateFlow<Audio?> = serviceConnection.currentAudioFlow
    val isPlaying: StateFlow<Boolean> = serviceConnection.isPlayingFlow


    init {
        observeFavouriteChanges()
    }

    private fun observeFavouriteChanges() {
        viewModelScope.launch {
            favourites.collect { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isEmpty = items.isEmpty()
                )
            }
        }
    }

    fun addToFavourites(audio: Audio) {
        viewModelScope.launch {
            try {
                addToFavouritesUseCase(audio)
                _uiState.value = _uiState.value.copy(
                    message = "Added to favourites"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun removeFromFavourites(audioId: String) {
        viewModelScope.launch {
            try {
                removeFromFavouritesUseCase(audioId)
                _uiState.value = _uiState.value.copy(
                    message = "Removed from favourites"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun toggleFavourite(audio: Audio) {
        viewModelScope.launch {
            try {
                toggleFavouriteUseCase(audio)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun isFavourite(audioId: String): Flow<Boolean> {
        return isFavouriteFlowUseCase(audioId)
    }

    fun playFavourite(favouriteAudio: FavouriteAudio) {
        serviceConnection.playAudio(favouriteAudio.audio)
    }

    fun playAllFavourites() {
        val items = favourites.value
        if (items.isNotEmpty()) {
            val audios = items.map { it.audio }
            serviceConnection.setPlaylist(audios)
            serviceConnection.playAudio(audios.first())
        }
    }

    fun shuffleFavourites() {
        val items = favourites.value
        if (items.isNotEmpty()) {
            val audios = items.map { it.audio }.shuffled()
            serviceConnection.setPlaylist(audios)
            serviceConnection.playAudio(audios.first())
        }
    }

    fun addFavouritesToQueue() {
        viewModelScope.launch {
            try {
                val items = favourites.value
                items.forEach { favourite ->
                    addToQueueUseCase(favourite.audio)
                }
                _uiState.value = _uiState.value.copy(
                    message = "Added ${items.size} songs to queue"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun searchFavourites(query: String): Flow<List<FavouriteAudio>> {
        return favourites.map { items ->
            if (query.isBlank()) {
                items
            } else {
                items.filter { favourite ->
                    favourite.audio.title.contains(query, ignoreCase = true) ||
                            favourite.audio.artist.contains(query, ignoreCase = true) ||
                            favourite.audio.album.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun loadDummyMusic() {
        _uiState.value = _uiState.value.copy(
            isEmpty = false,
            isLoading = false,
        )

        favourites = flowOf(sampleAudios.map { audio ->
            FavouriteAudio(
                id = audio.id,
                audioId = audio.id,
                audio = audio
            )
        }).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = sampleAudios.toFavAudio()
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class FavouriteUiState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = true,
    val error: String? = null,
    val message: String? = null
)