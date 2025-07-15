package com.faysal.zenify.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.faysal.zenify.domain.model.Audio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@UnstableApi
class MusicServiceConnection(private val context: Context) : ServiceConnection {

    private val _musicService = MutableStateFlow<MusicService?>(null)
    val musicService: StateFlow<MusicService?> = _musicService.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

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

    fun isPlaying(): Boolean {
        return _musicService.value?.isPlaying() ?: false
    }

    fun getCurrentAudio(): Audio? {
        return _musicService.value?.getCurrentAudio()
    }

    fun getPlaylist(): List<Audio> {
        return _musicService.value?.getPlaylist() ?: emptyList()
    }

    fun getCurrentPosition(): Long {
        return _musicService.value?.getCurrentPosition() ?: 0L
    }

    fun getDuration(): Long {
        return _musicService.value?.getDuration() ?: 0L
    }

    fun isRepeatEnabled(): Boolean {
        return _musicService.value?.isRepeatEnabled ?: false
    }

    fun isShuffleEnabled(): Boolean {
        return _musicService.value?.isShuffleEnabled ?: false
    }
}