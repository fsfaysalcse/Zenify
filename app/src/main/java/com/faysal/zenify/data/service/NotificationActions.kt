package com.faysal.zenify.data.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.faysal.zenify.R
import com.faysal.zenify.ui.util.ACTION_NEXT
import com.faysal.zenify.ui.util.ACTION_PLAY_PAUSE
import com.faysal.zenify.ui.util.ACTION_PREVIOUS
import com.faysal.zenify.ui.util.ACTION_REPEAT
import com.faysal.zenify.ui.util.ACTION_SHUFFLE

class NotificationActions(private val context: Context) {

    fun createPlayPauseAction(isPlaying: Boolean): NotificationCompat.Action {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val title = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Action.Builder(
            iconRes,
            title,
            createPendingIntent(ACTION_PLAY_PAUSE)
        ).build()
    }

    fun createPreviousAction(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.ic_previous,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        ).build()
    }

    fun createNextAction(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.ic_next,
            "Next",
            createPendingIntent(ACTION_NEXT)
        ).build()
    }

    fun createRepeatAction(isRepeatEnabled: Boolean): NotificationCompat.Action {
        val iconRes = if (isRepeatEnabled) R.drawable.ic_repeat_on else R.drawable.ic_repeat_off

        return NotificationCompat.Action.Builder(
            iconRes,
            "Repeat",
            createPendingIntent(ACTION_REPEAT)
        ).build()
    }

    fun createShuffleAction(isShuffleEnabled: Boolean): NotificationCompat.Action {
        val iconRes = if (isShuffleEnabled) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off

        return NotificationCompat.Action.Builder(
            iconRes,
            "Shuffle",
            createPendingIntent(ACTION_SHUFFLE)
        ).build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action).setPackage(context.packageName)
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun getAllActions(
        isPlaying: Boolean,
        isRepeatEnabled: Boolean,
        isShuffleEnabled: Boolean
    ): List<NotificationCompat.Action> {
        return listOf(
            createPreviousAction(),
            createPlayPauseAction(isPlaying),
            createNextAction(),
            createRepeatAction(isRepeatEnabled),
            createShuffleAction(isShuffleEnabled)
        )
    }
}