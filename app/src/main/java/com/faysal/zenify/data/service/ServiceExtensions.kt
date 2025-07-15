package com.faysal.zenify.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.faysal.zenify.R
import com.faysal.zenify.ui.util.CHANNEL_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.createNotificationChannel() {
    val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
        .setName(getString(R.string.music_notification_channel_name))
        .setDescription(getString(R.string.music_notification_channel_description))
        .setSound(null, null)
        .setVibrationEnabled(false)
        .build()

    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

suspend fun getEmbeddedCover(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.use {
            it.setDataSource(context, uri)
            val coverBytes = it.embeddedPicture
            coverBytes?.let { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        }
    } catch (e: Exception) {
        Log.e("ServiceExtensions", "Failed to extract embedded cover", e)
        null
    }
}

fun formatDuration(duration: Long): String {
    val minutes = duration / 60000
    val seconds = (duration % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}

fun Long.formatAsTime(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}