package com.faysal.zenify.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri

fun getEmbeddedCover(context: Context, uri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val art = retriever.embeddedPicture
        retriever.release()
        if (art != null) BitmapFactory.decodeByteArray(art, 0, art.size) else null
    } catch (e: Exception) {
        null
    }
}

fun formatDuration(duration: Long): String {
    val totalSec = duration / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%02d:%02d".format(min, sec)
}
