package com.faysal.zenify.data.service

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.faysal.zenify.domain.model.Audio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaylistManager {

    private val _playlist = MutableStateFlow<List<Audio>>(emptyList())
    val playlist: StateFlow<List<Audio>> = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _shuffledPlaylist = MutableStateFlow<List<Audio>>(emptyList())
    val shuffledPlaylist: StateFlow<List<Audio>> = _shuffledPlaylist.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    fun setPlaylist(audios: List<Audio>) {
        _playlist.value = audios
        if (_currentIndex.value >= audios.size || _currentIndex.value < 0) {
            _currentIndex.value = if (audios.isNotEmpty()) 0 else -1
        }
        updateShuffledPlaylist()
    }

    fun addToPlaylist(audio: Audio) {
        val currentList = _playlist.value.toMutableList()
        if (!currentList.contains(audio)) {
            currentList.add(audio)
            _playlist.value = currentList
            updateShuffledPlaylist()
        }
    }

    fun removeFromPlaylist(audio: Audio) {
        val currentList = _playlist.value.toMutableList()
        val removedIndex = currentList.indexOf(audio)
        if (removedIndex != -1) {
            currentList.removeAt(removedIndex)
            _playlist.value = currentList

            if (_currentIndex.value == removedIndex) {
                _currentIndex.value = if (currentList.isNotEmpty())
                    removedIndex.coerceAtMost(currentList.size - 1) else -1
            } else if (_currentIndex.value > removedIndex) {
                _currentIndex.value = _currentIndex.value - 1
            }

            updateShuffledPlaylist()
        }
    }

    fun setCurrentIndex(index: Int) {
        if (index >= 0 && index < _playlist.value.size) {
            _currentIndex.value = index
        }
    }

    fun setShuffleEnabled(enabled: Boolean) {
        _isShuffleEnabled.value = enabled
        updateShuffledPlaylist()
    }

    fun getCurrentAudio(): Audio? {
        val currentList = getCurrentPlaylist()
        val index = _currentIndex.value
        return if (index >= 0 && index < currentList.size) {
            currentList[index]
        } else null
    }

    fun getCurrentPlaylist(): List<Audio> {
        return if (_isShuffleEnabled.value) _shuffledPlaylist.value else _playlist.value
    }

    fun hasNext(): Boolean {
        val currentList = getCurrentPlaylist()
        return _currentIndex.value < currentList.size - 1
    }

    fun hasPrevious(): Boolean {
        return _currentIndex.value > 0
    }

    fun getNextIndex(): Int {
        val currentList = getCurrentPlaylist()
        return if (_currentIndex.value < currentList.size - 1) {
            _currentIndex.value + 1
        } else -1
    }

    fun getPreviousIndex(): Int {
        return if (_currentIndex.value > 0) {
            _currentIndex.value - 1
        } else -1
    }

    fun createMediaItems(): List<MediaItem> {
        return getCurrentPlaylist().map { audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaId(audio.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(audio.title)
                        .setArtist(audio.artist)
                        .setAlbumTitle(audio.album)
                        .build()
                )
                .build()
        }
    }

    fun findAudioIndex(audio: Audio): Int {
        return getCurrentPlaylist().indexOf(audio)
    }

    private fun updateShuffledPlaylist() {
        if (_isShuffleEnabled.value) {
            _shuffledPlaylist.value = _playlist.value.shuffled()
        } else {
            _shuffledPlaylist.value = _playlist.value
        }
    }

    fun isEmpty(): Boolean = _playlist.value.isEmpty()

    fun size(): Int = _playlist.value.size

    fun clear() {
        _playlist.value = emptyList()
        _shuffledPlaylist.value = emptyList()
        _currentIndex.value = -1
    }
}