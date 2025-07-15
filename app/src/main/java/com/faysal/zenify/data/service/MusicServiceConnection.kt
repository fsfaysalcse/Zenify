package com.faysal.zenify.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.domain.model.Audio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@UnstableApi
class MusicServiceConnection(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) : ServiceConnection {

    private val _musicService = MutableStateFlow<MusicService?>(null)
    val musicService: StateFlow<MusicService?> = _musicService.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    val isPlayingFlow: StateFlow<Boolean> = _musicService.flatMapLatest { service ->
        service?.isPlayingFlow ?: MutableStateFlow(false)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val currentAudioFlow: StateFlow<Audio?> = _musicService.flatMapLatest { service ->
        service?.currentAudioFlow ?: MutableStateFlow(null)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), null)

    val currentPositionFlow: StateFlow<Long> = _musicService.flatMapLatest { service ->
        service?.currentPositionFlow ?: MutableStateFlow(0L)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0L)

    val durationFlow: StateFlow<Long> = _musicService.flatMapLatest { service ->
        service?.durationFlow ?: MutableStateFlow(0L)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), 0L)

    val isRepeatEnabledFlow: StateFlow<Boolean> = _musicService.flatMapLatest { service ->
        service?.isRepeatEnabledFlow ?: MutableStateFlow(false)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val isShuffleEnabledFlow: StateFlow<Boolean> = _musicService.flatMapLatest { service ->
        service?.isShuffleEnabledFlow ?: MutableStateFlow(false)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), false)

    val playlistFlow: StateFlow<List<Audio>> = _musicService.flatMapLatest { service ->
        service?.playlistFlow ?: MutableStateFlow(emptyList())
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as? MusicService.MusicBinder
        _musicService.value = binder?.getService()
        _isConnected.value = true
        Log.d("MusicServiceConnection", "Service connected")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _musicService.value = null
        _isConnected.value = false
        Log.d("MusicServiceConnection", "Service disconnected")
    }

    fun bindService() {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        context.startService(intent)
    }

    fun unbindService() {
        if (_isConnected.value) {
            context.unbindService(this)
            _isConnected.value = false
        }
    }

    fun playAudio(audio: Audio) {
        _musicService.value?.playAudio(audio)
    }

    fun setPlaylist(audios: List<Audio>) {
        _musicService.value?.setPlaylist(audios)
    }

    fun playNext() {
        _musicService.value?.playNext()
    }

    fun playPrevious() {
        _musicService.value?.playPrevious()
    }

    fun pauseAudio() {
        _musicService.value?.pauseAudio()
    }

    fun resumeAudio() {
        _musicService.value?.resumeAudio()
    }

    fun toggleRepeat() {
        _musicService.value?.toggleRepeat()
    }

    fun toggleShuffle() {
        _musicService.value?.toggleShuffle()
    }

    fun seekTo(position: Long) {
        _musicService.value?.seekTo(position)
    }
}
