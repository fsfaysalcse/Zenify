package com.faysal.zenify.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.faysal.zenify.R

fun getEmbeddedCover(context: Context, uri: Uri?): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val art = retriever.embeddedPicture
        retriever.release()
        if (art != null) BitmapFactory.decodeByteArray(art, 0, art.size) else null
    } catch (e: Exception) {
        ContextCompat.getDrawable(context, R.drawable.default_cover)?.toBitmap()
    }
}

fun formatDuration(duration: Long): String {
    val totalSec = duration / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%02d:%02d".format(min, sec)
}


private fun formatFileSize(size: Long): String {
    val kb = size / 1024
    val mb = kb / 1024
    return when {
        mb > 0 -> "%.1f MB".format(mb.toFloat())
        kb > 0 -> "$kb KB"
        else -> "$size B"
    }
}

