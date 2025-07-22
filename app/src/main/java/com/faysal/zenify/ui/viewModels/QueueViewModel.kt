package com.faysal.zenify.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.QueueItem
import com.faysal.zenify.domain.usecases.AddToQueueNextUseCase
import com.faysal.zenify.domain.usecases.AddToQueueUseCase
import com.faysal.zenify.domain.usecases.ClearQueueUseCase
import com.faysal.zenify.domain.usecases.GetQueueItemsUseCase
import com.faysal.zenify.domain.usecases.MoveQueueItemUseCase
import com.faysal.zenify.domain.usecases.RemoveFromQueueUseCase
import com.faysal.zenify.ui.states.QueueUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@UnstableApi
class QueueViewModel(
    private val serviceConnection: MusicServiceConnection,
    private val getQueueItemsUseCase: GetQueueItemsUseCase,
    private val addToQueueUseCase: AddToQueueUseCase,
    private val addToQueueNextUseCase: AddToQueueNextUseCase,
    private val removeFromQueueUseCase: RemoveFromQueueUseCase,
    private val moveQueueItemUseCase: MoveQueueItemUseCase,
    private val clearQueueUseCase: ClearQueueUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueueUiState())
    val uiState: StateFlow<QueueUiState> = _uiState.asStateFlow()

    val queueItems: StateFlow<List<QueueItem>> = getQueueItemsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentAudio: StateFlow<Audio?> = serviceConnection.currentAudioFlow
    val isPlaying: StateFlow<Boolean> = serviceConnection.isPlayingFlow
    val currentPosition: StateFlow<Long> = serviceConnection.currentPositionFlow
    val duration: StateFlow<Long> = serviceConnection.durationFlow

    init {
        observeQueueChanges()
    }

    private fun observeQueueChanges() {
        viewModelScope.launch {
            queueItems.collect { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isEmpty = items.isEmpty()
                )
            }
        }
    }

    fun addToQueue(audio: Audio) {
        viewModelScope.launch {
            try {
                addToQueueUseCase(audio)
                _uiState.value = _uiState.value.copy(
                    message = "Added to queue"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun playFromQueue(queueItem: QueueItem) {
        serviceConnection.playAudio(queueItem.audio)
    }

    fun playQueue() {
        val items = queueItems.value
        if (items.isNotEmpty()) {
            val audios = items.map { it.audio }
            serviceConnection.setPlaylist(audios)
            serviceConnection.playAudio(audios.first())
        }
    }

    fun shuffleQueue() {
        val items = queueItems.value
        if (items.isNotEmpty()) {
            val audios = items.map { it.audio }.shuffled()
            serviceConnection.setPlaylist(audios)
            serviceConnection.playAudio(audios.first())
        }
    }

    private fun getCurrentQueueIndex(): Int {
        val currentAudio = currentAudio.value ?: return -1
        val items = queueItems.value
        return items.indexOfFirst { it.audio.id == currentAudio.id }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun addToQueueNext(audio: Audio) {
        viewModelScope.launch {
            try {
                val currentIndex = getCurrentQueueIndex()
                addToQueueNextUseCase(audio, currentIndex)
                _uiState.value = _uiState.value.copy(
                    message = "Added next in queue"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }


    fun removeFromQueue(queueItemId: String) {
        viewModelScope.launch {
            try {
                removeFromQueueUseCase(queueItemId)
                _uiState.value = _uiState.value.copy(
                    message = "Removed from queue"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }


    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            try {
                val items = queueItems.value
                if (fromIndex in items.indices && toIndex in items.indices) {
                    val item = items[fromIndex]
                    moveQueueItemUseCase(fromIndex, toIndex, item.id)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun clearQueue() {
        viewModelScope.launch {
            try {
                clearQueueUseCase()
                _uiState.value = _uiState.value.copy(
                    message = "Queue cleared"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }
}

