package com.faysal.zenify.data.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.faysal.zenify.domain.repository.AudioRepository
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.util.getEmbeddedCover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AudioRepositoryImpl(
    private val context: Context
) : AudioRepository {

    override fun getAllAudio(): List<Audio> {

        val list = mutableListOf<Audio>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        ) ?: return emptyList()

        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val year = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val title = cursor.getString(titleCol)
            val artist = cursor.getString(artistCol)
            val album = cursor.getString(albumCol)
            val duration = cursor.getLong(durationCol)
            val year = cursor.getInt(durationCol)
            val contentUri: Uri =
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
            list.add(Audio(id.toString(), title, artist, album, duration, year.toString(),contentUri))
        }

        cursor.close()
        return list
    }

    override suspend fun getSongCover(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        getEmbeddedCover(context, uri)
    }


}