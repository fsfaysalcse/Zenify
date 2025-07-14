package com.faysal.zenify.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.faysal.zenify.MainActivity
import com.faysal.zenify.R
import com.faysal.zenify.ui.model.Audio
import com.faysal.zenify.ui.util.getEmbeddedCover


@OptIn(UnstableApi::class)
class MusicService : MediaSessionService() {
    private val binder = MusicBinder()
    var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var notificationManager: PlayerNotificationManager? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "music_playback_channel"
    }

    override fun onCreate() {
        super.onCreate()
        try {
            player = ExoPlayer.Builder(this).build()
            mediaSession = MediaSession.Builder(this, player!!).build()

            notificationManager = PlayerNotificationManager.Builder(
                this,
                NOTIFICATION_ID,
                CHANNEL_ID
            )
                .setChannelNameResourceId(R.string.app_name)
                .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationPosted(
                        notificationId: Int,
                        notification: android.app.Notification,
                        ongoing: Boolean
                    ) {
                        try {
                            startForeground(notificationId, notification)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            stopSelf()
                        }
                    }

                    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                        stopSelf()
                    }
                })
                .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): String {
                        return player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown"
                    }

                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        val intent = Intent(this@MusicService, MainActivity::class.java)
                        return PendingIntent.getActivity(
                            this@MusicService,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }

                    override fun getCurrentContentText(player: Player): String? {
                        return player.currentMediaItem?.mediaMetadata?.artist?.toString()
                    }

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback
                    ): android.graphics.Bitmap? {
                        val uri = player.currentMediaItem?.localConfiguration?.uri
                        return uri?.let { getEmbeddedCover(this@MusicService, it) }
                    }
                })
                .build()

            //Token


            notificationManager?.setMediaSessionToken(mediaSession!!.platformToken)
            notificationManager?.setPlayer(player)
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    fun playAudio(audio: Audio) {
        try {
            val mediaItem = MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(audio.title)
                        .setArtist(audio.artist)
                        .build()
                )
                .build()
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseAudio() {
        try {
            player?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resumeAudio() {
        try {
            player?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isPlaying(): Boolean {
        return try {
            player?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            intent?.let {
                val uri = it.getStringExtra("AUDIO_URI")
                val title = it.getStringExtra("AUDIO_TITLE")
                val artist = it.getStringExtra("AUDIO_ARTIST")
                if (uri != null) {
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(title)
                                .setArtist(artist)
                                .build()
                        )
                        .build()
                    player?.setMediaItem(mediaItem)
                    player?.prepare()
                    player?.play()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            player?.release()
            mediaSession?.release()
            notificationManager?.setPlayer(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}