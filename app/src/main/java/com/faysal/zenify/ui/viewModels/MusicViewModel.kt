package com.faysal.zenify.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@UnstableApi
class MusicViewModel(
    private val serviceConnection: MusicServiceConnection,
    private val getAudiosUseCase: GetAudiosUseCase
) : ViewModel() {


    private val _audios = MutableStateFlow<List<Audio>>(emptyList())
    val audios: StateFlow<List<Audio>> get() = _audios

    private val _currentAudio = MutableStateFlow<Audio?>(null)
    val currentAudio: StateFlow<Audio?> get() = _currentAudio

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _playlist = MutableStateFlow<List<Audio>>(emptyList())
    val playlist: StateFlow<List<Audio>> = _playlist.asStateFlow()

    init {
        serviceConnection.bindService()
        observeServiceConnection()
        loadAudios()
    }

    private fun observeServiceConnection() {
        viewModelScope.launch {
            serviceConnection.isConnected.collect { connected ->
                if (connected) updatePlaybackState()
            }
        }
    }

    private fun updatePlaybackState() {
        viewModelScope.launch {
            _isPlaying.value = serviceConnection.isPlaying()
            _currentAudio.value = serviceConnection.getCurrentAudio()
            _currentPosition.value = serviceConnection.getCurrentPosition()
            _duration.value = serviceConnection.getDuration()
            _isRepeatEnabled.value = serviceConnection.isRepeatEnabled()
            _isShuffleEnabled.value = serviceConnection.isShuffleEnabled()
            _playlist.value = serviceConnection.getPlaylist()
        }
    }

    fun loadAudios() {
        viewModelScope.launch {
            _audios.value = runCatching { getAudiosUseCase() }.getOrElse { emptyList() }
        }
    }

    fun playAudio(audio: Audio) {
        serviceConnection.playAudio(audio)
        updatePlaybackState()
    }

    fun setPlaylist(audios: List<Audio>) {
        serviceConnection.setPlaylist(audios)
        updatePlaybackState()
    }

    fun playPause() {
        if (_isPlaying.value) {
            serviceConnection.pauseAudio()
        } else {
            serviceConnection.resumeAudio()
        }
        updatePlaybackState()
    }

    fun playNext() {
        serviceConnection.playNext()
        updatePlaybackState()
    }

    fun playPrevious() {
        serviceConnection.playPrevious()
        updatePlaybackState()
    }

    fun toggleRepeat() {
        serviceConnection.toggleRepeat()
        updatePlaybackState()
    }

    fun toggleShuffle() {
        serviceConnection.toggleShuffle()
        updatePlaybackState()
    }

    fun seekTo(position: Long) {
        serviceConnection.seekTo(position)
        _currentPosition.value = position
    }

    fun refreshPlaybackState() {
        updatePlaybackState()
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unbindService()
    }
}