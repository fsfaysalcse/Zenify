package com.faysal.zenify.ui.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.data.service.MusicServiceConnection
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.ui.states.MusicScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@UnstableApi
class MusicViewModel(
    private val serviceConnection: MusicServiceConnection,
    private val getAudiosUseCase: GetAudiosUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _audios = MutableStateFlow<List<Audio>>(emptyList())
    val audios: StateFlow<List<Audio>> get() = _audios.asStateFlow()

    private val _currentAudio = MutableStateFlow<Audio?>(null)
    val currentAudio: StateFlow<Audio?> get() = _currentAudio.asStateFlow()

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

    private val _backStack = mutableStateListOf<MusicScreen>()
    val backStack: SnapshotStateList<MusicScreen> = _backStack

    init {
        serviceConnection.bindService()
        observeServiceConnection()
        loadAudios()

        val saved = savedStateHandle.get<List<String>>("backStack")
        if (saved != null) {
            _backStack.addAll(saved.mapNotNull { MusicScreen.fromString(it) })
        } else {
            _backStack.add(MusicScreen.AlbumList)
        }
    }

    fun push(screen: MusicScreen) {
        _backStack.add(screen)
        saveBackStack()
    }

    fun pop(): Boolean {
        return if (_backStack.size > 1) {
            _backStack.removeAt(_backStack.lastIndex)
            saveBackStack()
            true
        } else {
            false
        }
    }

    fun resetBackStack(rootScreen: MusicScreen) {
        backStack.clear()
        backStack.add(rootScreen)
        saveBackStack()
    }

    private fun saveBackStack() {
        val stringList = _backStack.map { it.toString() }
        savedStateHandle.set("backStack", stringList)
    }

    private fun observeServiceConnection() {
        viewModelScope.launch {
            serviceConnection.isConnected.collect { connected ->
                if (!connected) {
                    _isPlaying.value = false
                    _currentAudio.value = null
                    _currentPosition.value = 0L
                    _duration.value = 0L
                    _isRepeatEnabled.value = false
                    _isShuffleEnabled.value = false
                    _playlist.value = emptyList()
                }
            }
        }

        viewModelScope.launch {
            serviceConnection.isPlayingFlow.collect { playing ->
                _isPlaying.value = playing
            }
        }

        viewModelScope.launch {
            serviceConnection.currentAudioFlow.collect { audio ->
                _currentAudio.value = audio
            }
        }

        viewModelScope.launch {
            serviceConnection.currentPositionFlow.collect { position ->
                _currentPosition.value = position
            }
        }

        viewModelScope.launch {
            serviceConnection.durationFlow.collect { dur ->
                _duration.value = dur
            }
        }

        viewModelScope.launch {
            serviceConnection.isRepeatEnabledFlow.collect { repeatEnabled ->
                _isRepeatEnabled.value = repeatEnabled
            }
        }

        viewModelScope.launch {
            serviceConnection.isShuffleEnabledFlow.collect { shuffleEnabled ->
                _isShuffleEnabled.value = shuffleEnabled
            }
        }

        viewModelScope.launch {
            serviceConnection.playlistFlow.collect { playlist ->
                _playlist.value = playlist
            }
        }
    }

    fun loadAudios() {
        viewModelScope.launch {
            _audios.value = runCatching { getAudiosUseCase() }.getOrElse { emptyList() }
        }
    }

    fun playAudio(audio: Audio) {
        serviceConnection.playAudio(audio)
    }

    fun setPlaylist(audios: List<Audio>) {
        serviceConnection.setPlaylist(audios)
    }

    fun playPause() {
        if (_isPlaying.value) {
            serviceConnection.pauseAudio()
        } else {
            serviceConnection.resumeAudio()
        }
    }

    fun playNext() {
        serviceConnection.playNext()
    }

    fun playPrevious() {
        serviceConnection.playPrevious()
    }

    fun toggleRepeat() {
        serviceConnection.toggleRepeat()
    }

    fun toggleShuffle() {
        serviceConnection.toggleShuffle()
    }

    fun seekTo(position: Long) {
        serviceConnection.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unbindService()
    }
}
