package com.faysal.zenify.data.service


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.faysal.zenify.MainActivity
import com.faysal.zenify.R
import com.faysal.zenify.ui.util.ACTION_REPEAT
import com.faysal.zenify.ui.util.ACTION_SHUFFLE
import com.faysal.zenify.ui.util.CHANNEL_ID
import com.faysal.zenify.ui.util.NOTIFICATION_ID
import com.faysal.zenify.ui.util.getEmbeddedCover

@UnstableApi
class MusicNotificationManager(
    private val context: Context,
    private val service: MusicService,
    private val onNotificationPosted: (Int, android.app.Notification) -> Unit,
    private val onNotificationCancelled: (Int, Boolean) -> Unit
) {

    private var notificationManager: PlayerNotificationManager? = null

    fun createNotificationManager(): PlayerNotificationManager {
        return PlayerNotificationManager.Builder(context, NOTIFICATION_ID, CHANNEL_ID)
            .setChannelNameResourceId(R.string.app_name)
            .setNotificationListener(createNotificationListener())
            .setMediaDescriptionAdapter(createMediaDescriptionAdapter())
            .setCustomActionReceiver(createCustomActionReceiver())
            .build()
            .also { manager ->
                notificationManager = manager
                configureNotificationActions(manager)
            }
    }

    private fun createNotificationListener(): PlayerNotificationManager.NotificationListener {
        return object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(
                notificationId: Int,
                notification: android.app.Notification,
                ongoing: Boolean
            ) {
                onNotificationPosted(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                onNotificationCancelled(notificationId, dismissedByUser)
            }
        }
    }

    private fun createMediaDescriptionAdapter(): PlayerNotificationManager.MediaDescriptionAdapter {
        return object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): String {
                return player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Unknown"
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                return PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            override fun getCurrentContentText(player: Player): String? {
                return player.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist"
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return try {
                    val uri = player.currentMediaItem?.localConfiguration?.uri
                    uri?.let { getEmbeddedCover(context, it) }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun createCustomActionReceiver(): PlayerNotificationManager.CustomActionReceiver {
        return object : PlayerNotificationManager.CustomActionReceiver {
            override fun createCustomActions(
                context: Context,
                instanceId: Int
            ): Map<String, NotificationCompat.Action> {
                return mapOf(
                    ACTION_REPEAT to NotificationCompat.Action.Builder(
                        if (service.isRepeatEnabled) R.drawable.ic_repeat_on else R.drawable.ic_repeat_off,
                        context.getString(R.string.repeat),
                        createPendingIntent(context, ACTION_REPEAT, instanceId)
                    ).build(),
                    ACTION_SHUFFLE to NotificationCompat.Action.Builder(
                        if (service.isShuffleEnabled) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off,
                        context.getString(R.string.shuffle),
                        createPendingIntent(context, ACTION_SHUFFLE, instanceId)
                    ).build()
                )
            }

            override fun getCustomActions(player: Player): List<String> {
                return listOf(ACTION_REPEAT, ACTION_SHUFFLE)
            }

            override fun onCustomAction(player: Player, action: String, intent: Intent) {
                when (action) {
                    ACTION_REPEAT -> service.toggleRepeat()
                    ACTION_SHUFFLE -> service.toggleShuffle()
                }
                notificationManager?.invalidate()
            }
        }
    }

    private fun createPendingIntent(context: Context, action: String, instanceId: Int): PendingIntent {
        val intent = Intent(action).setPackage(context.packageName)
        return PendingIntent.getBroadcast(
            context,
            instanceId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun configureNotificationActions(manager: PlayerNotificationManager) {
        manager.apply {
            setUsePreviousAction(true)
            setUsePlayPauseActions(true)
            setUseNextAction(true)
            setUseFastForwardAction(false)
            setUseRewindAction(false)
            setUseStopAction(false)
        }
    }

    fun invalidate() {
        notificationManager?.invalidate()
    }

    fun setPlayer(player: Player?) {
        notificationManager?.setPlayer(player)
    }

    fun setMediaSession(session: MediaSession?) {
        session?.let {
            notificationManager?.setMediaSessionToken(it.platformToken)
        }
    }

}