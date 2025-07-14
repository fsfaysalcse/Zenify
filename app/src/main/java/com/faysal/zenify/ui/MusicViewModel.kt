package com.faysal.zenify.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faysal.zenify.domain.usecases.GetAudiosUseCase
import com.faysal.zenify.service.MusicService
import com.faysal.zenify.ui.model.Audio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class MusicViewModel(
    private val getAudiosUseCase: GetAudiosUseCase
) : ViewModel() {
    private val _audios = MutableStateFlow<List<Audio>>(emptyList())
    val audios: StateFlow<List<Audio>> get() = _audios

    private val _currentAudio = MutableStateFlow<Audio?>(null)
    val currentAudio: StateFlow<Audio?> get() = _currentAudio

    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            musicService = null
        }
    }

    fun loadAudios(context: Context) {
        viewModelScope.launch {
            try {
                _audios.value = getAudiosUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
                _audios.value = emptyList()
            }
        }
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun playAudio(context: Context, audio: Audio) {
        _currentAudio.value = audio
        musicService?.playAudio(audio)
        val intent = Intent(context, MusicService::class.java).apply {
            putExtra("AUDIO_URI", audio.uri.toString())
            putExtra("AUDIO_TITLE", audio.title)
            putExtra("AUDIO_ARTIST", audio.artist)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        }else{
            context.startService(intent)
        }
    }

    fun pauseAudio() {
        musicService?.pauseAudio()
    }

    fun resumeAudio() {
        musicService?.resumeAudio()
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            musicService?.baseContext?.unbindService(connection)
            isBound = false
        }
    }
}