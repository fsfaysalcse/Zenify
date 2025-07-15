package com.faysal.zenify.domain.repository
import android.graphics.Bitmap
import android.net.Uri
import com.faysal.zenify.domain.model.Audio

class FakeAudioRepository : AudioRepository {

    override fun getAllAudio(): List<Audio> {
        return listOf(
            Audio(
                id = 1L,
                title = "Test Song 1",
                artist = "Artist A",
                album = "Album X",
                duration = 215000,
                uri = Uri.EMPTY
            ),
            Audio(
                id = 2L,
                title = "Test Song 2",
                artist = "Artist B",
                album = "Album Y",
                duration = 184000,
                uri = Uri.EMPTY
            )
        )
    }

    override suspend fun getSongCover(uri: Uri): Bitmap? {
       return null
    }
}
