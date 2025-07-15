package com.faysal.zenify.domain.repository


import android.graphics.Bitmap
import android.net.Uri
import com.faysal.zenify.domain.model.Audio

interface AudioRepository {
    fun getAllAudio(): List<Audio>
    suspend fun getSongCover(uri: Uri): Bitmap?
}
