package com.faysal.zenify.data.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.faysal.zenify.data.datastore.PlaybackStateManager
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.util.ACTION_REPEAT
import com.faysal.zenify.ui.util.ACTION_SHUFFLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.UUID

private const val TAG = "MusicService"

@UnstableApi
class MusicService : MediaLibraryService() {

    private val binder = MusicBinder()
    private var player: ExoPlayer? = null
    private var mediaSession: MediaLibrarySession? = null
    private var notificationManager: MusicNotificationManager? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var playlistManager: PlaylistManager
    private lateinit var playbackStateManager: PlaybackStateManager
    private val mediaLibraryCallback: MediaLibraryCallback by inject()

    var isRepeatEnabled = false
        private set
    var isShuffleEnabled = false
        private set

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_REPEAT -> toggleRepeat()
                ACTION_SHUFFLE -> toggleShuffle()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    private fun initializeService() {
        try {
            playlistManager = PlaylistManager()
            playbackStateManager = PlaybackStateManager(this)

            registerBroadcastReceiver()
            initializePlayer()
            initializeMediaSession()
            initializeNotificationManager()
            restorePlaybackState()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MusicService", e)
            stopSelf()
        }
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_REPEAT)
            addAction(ACTION_SHUFFLE)
        }
        ContextCompat.registerReceiver(this, broadcastReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        player?.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                handleMediaItemTransition(mediaItem)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                handlePlaybackStateChanged(playbackState)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                serviceScope.launch {
                    playbackStateManager.savePlayingState(isPlaying)
                }
            }
        })
    }

    private fun handleMediaItemTransition(mediaItem: MediaItem?) {
        serviceScope.launch {
            val newIndex = player?.currentMediaItemIndex ?: -1
            playlistManager.setCurrentIndex(newIndex)
            playbackStateManager.saveCurrentIndex(newIndex)
        }
    }

    private fun handlePlaybackStateChanged(playbackState: Int) {
        serviceScope.launch {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    if (isRepeatEnabled) {
                        player?.seekTo(0, 0)
                        player?.play()
                    }
                }
                Player.STATE_READY -> {
                    playbackStateManager.saveCurrentPosition(player?.currentPosition ?: 0L)
                }
            }
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaLibrarySession.Builder(this, player!!, mediaLibraryCallback)
            .setId(UUID.randomUUID().toString())
            .build()
    }

    private fun initializeNotificationManager() {
        notificationManager = MusicNotificationManager(
            context = this,
            service = this,
            onNotificationPosted = { notificationId, notification ->
                handleNotificationPosted(notificationId, notification)
            },
            onNotificationCancelled = { notificationId, dismissedByUser ->
                handleNotificationCancelled(notificationId, dismissedByUser)
            }
        )

        val manager = notificationManager!!.createNotificationManager()
        manager.setMediaSessionToken(mediaSession!!.platformToken)
        manager.setPlayer(player)
    }

    private fun handleNotificationPosted(notificationId: Int, notification: Notification) {
        try {
            startForeground(notificationId, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service", e)
            stopSelf()
        }
    }

    private fun handleNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        if (dismissedByUser) {
            pauseAudio()
        }
    }

    private fun restorePlaybackState() {
        serviceScope.launch {
            try {
                val savedIndex = playbackStateManager.currentIndex.first()
                val savedPosition = playbackStateManager.currentPosition.first()
                val savedRepeatState = playbackStateManager.isRepeatEnabled.first()
                val savedShuffleState = playbackStateManager.isShuffleEnabled.first()
                val savedPlayingState = playbackStateManager.isPlaying.first()

                isRepeatEnabled = savedRepeatState
                isShuffleEnabled = savedShuffleState

                player?.repeatMode = if (isRepeatEnabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                player?.shuffleModeEnabled = isShuffleEnabled
                playlistManager.setShuffleEnabled(isShuffleEnabled)

                if (savedIndex >= 0 && !playlistManager.isEmpty()) {
                    playlistManager.setCurrentIndex(savedIndex)
                    updatePlayerMediaItems()

                    if (savedPosition > 0) {
                        player?.seekTo(savedPosition)
                    }

                    if (savedPlayingState) {
                        player?.play()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restore playback state", e)
            }
        }
    }

    fun playAudio(audio: Audio) {
        serviceScope.launch {
            try {
                playlistManager.addToPlaylist(audio)
                val index = playlistManager.findAudioIndex(audio)
                if (index >= 0) {
                    playlistManager.setCurrentIndex(index)
                    updatePlayerMediaItems()
                    player?.seekTo(index, 0)
                    player?.play()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play audio: ${audio.title}", e)
            }
        }
    }

    fun setPlaylist(audios: List<Audio>) {
        serviceScope.launch {
            try {
                playlistManager.setPlaylist(audios)
                updatePlayerMediaItems()
                if (audios.isNotEmpty()) {
                    player?.play()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set playlist", e)
            }
        }
    }

    private fun updatePlayerMediaItems() {
        try {
            val mediaItems = playlistManager.createMediaItems()
            val currentIndex = playlistManager.currentIndex.value

            if (mediaItems.isNotEmpty()) {
                player?.setMediaItems(mediaItems, currentIndex.coerceAtLeast(0), 0)
                player?.prepare()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update player media items", e)
        }
    }

    fun playNext() {
        serviceScope.launch {
            try {
                if (playlistManager.hasNext()) {
                    val nextIndex = playlistManager.getNextIndex()
                    if (nextIndex >= 0) {
                        playlistManager.setCurrentIndex(nextIndex)
                        player?.seekToNextMediaItem()
                        player?.play()
                    }
                } else if (isRepeatEnabled) {
                    playlistManager.setCurrentIndex(0)
                    player?.seekTo(0, 0)
                    player?.play()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play next track", e)
            }
        }
    }

    fun playPrevious() {
        serviceScope.launch {
            try {
                if (playlistManager.hasPrevious()) {
                    val previousIndex = playlistManager.getPreviousIndex()
                    if (previousIndex >= 0) {
                        playlistManager.setCurrentIndex(previousIndex)
                        player?.seekToPreviousMediaItem()
                        player?.play()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play previous track", e)
            }
        }
    }

    fun pauseAudio() {
        try {
            player?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pause audio", e)
        }
    }

    fun resumeAudio() {
        try {
            player?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume audio", e)
        }
    }

    fun toggleRepeat() {
        serviceScope.launch {
            try {
                isRepeatEnabled = !isRepeatEnabled
                player?.repeatMode = if (isRepeatEnabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                playbackStateManager.saveRepeatState(isRepeatEnabled)
                notificationManager?.invalidate()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle repeat", e)
            }
        }
    }

    fun toggleShuffle() {
        serviceScope.launch {
            try {
                isShuffleEnabled = !isShuffleEnabled
                player?.shuffleModeEnabled = isShuffleEnabled
                playlistManager.setShuffleEnabled(isShuffleEnabled)
                playbackStateManager.saveShuffleState(isShuffleEnabled)
                updatePlayerMediaItems()
                notificationManager?.invalidate()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle shuffle", e)
            }
        }
    }

    fun isPlaying(): Boolean {
        return try {
            player?.isPlaying == true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check playback state", e)
            false
        }
    }

    fun getCurrentAudio(): Audio? = playlistManager.getCurrentAudio()

    fun getPlaylist(): List<Audio> = playlistManager.getCurrentPlaylist()

    fun seekTo(position: Long) {
        serviceScope.launch {
            try {
                player?.seekTo(position)
                playbackStateManager.saveCurrentPosition(position)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to seek to position", e)
            }
        }
    }

    fun getCurrentPosition(): Long = player?.currentPosition ?: 0L

    fun getDuration(): Long = player?.duration ?: 0L

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        handleStartCommand(intent)
        return START_STICKY
    }

    private fun handleStartCommand(intent: Intent?) {
        serviceScope.launch {
            try {
                intent?.let { startIntent ->
                    val uri = startIntent.getStringExtra("AUDIO_URI")
                    val title = startIntent.getStringExtra("AUDIO_TITLE")
                    val artist = startIntent.getStringExtra("AUDIO_ARTIST")

                    if (uri != null) {
                        val audio = Audio(
                            id = System.currentTimeMillis(),
                            title = title ?: "Unknown",
                            artist = artist ?: "Unknown",
                            album = "",
                            uri = Uri.parse(uri),
                            duration = 0L
                        )
                        playAudio(audio)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to handle start command", e)
            }
        }
    }

    override fun onDestroy() {
        try {
            serviceScope.launch {
                playbackStateManager.saveCurrentPosition(player?.currentPosition ?: 0L)
                playbackStateManager.savePlayingState(player?.isPlaying == true)
            }

            player?.release()
            mediaSession?.release()
            notificationManager?.setPlayer(null)
            unregisterReceiver(broadcastReceiver)
            serviceScope.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to destroy MusicService", e)
        }
        super.onDestroy()
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}