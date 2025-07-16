package com.faysal.zenify.domain.repository
import android.graphics.Bitmap
import android.net.Uri
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.ui.util.sampleAudios

class FakeAudioRepository : AudioRepository {

    override fun getAllAudio(): List<Audio> {
        return sampleAudios
    }

    override suspend fun getSongCover(uri: Uri): Bitmap? {
       return null
    }
}
