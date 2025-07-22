package com.faysal.zenify.data.datastore

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.SerializableAudio
import com.faysal.zenify.ui.util.PLAYLIST_DATASTORE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.playlistDataStore: DataStore<Preferences> by preferencesDataStore(name = PLAYLIST_DATASTORE)


class PlaylistDataStore(private val context: Context) {

    private val playlistKey = stringPreferencesKey("playlist")
    private val lastPlayedKey = longPreferencesKey("last_played_id")

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val playlist: Flow<List<Audio>> = context.playlistDataStore.data
        .map { preferences ->
            val playlistJson = preferences[playlistKey] ?: "[]"
            try {
                val serializableAudios = json.decodeFromString<List<SerializableAudio>>(playlistJson)
                serializableAudios.map { it.toAudio() }
            } catch (e: Exception) {
                emptyList()
            }
        }

    val lastPlayedId: Flow<Long> = context.playlistDataStore.data
        .map { preferences -> preferences[lastPlayedKey] ?: -1L }

    suspend fun savePlaylist(audios: List<Audio>) {
        context.playlistDataStore.edit { preferences ->
            val serializableAudios = audios.map { it.toSerializableAudio() }
            val playlistJson = json.encodeToString(serializableAudios)
            preferences[playlistKey] = playlistJson
        }
    }

    suspend fun saveLastPlayedId(id: Long) {
        context.playlistDataStore.edit { preferences ->
            preferences[lastPlayedKey] = id
        }
    }

    suspend fun clearPlaylist() {
        context.playlistDataStore.edit { preferences ->
            preferences.remove(playlistKey)
            preferences.remove(lastPlayedKey)
        }
    }

    private fun Audio.toSerializableAudio(): SerializableAudio {
        return SerializableAudio(
            id = id,
            title = title,
            artist = artist,
            album = album,
            uri = uri.toString(),
            duration = duration
        )
    }

    private fun SerializableAudio.toAudio(): Audio {
        return Audio(
            id = id,
            title = title,
            artist = artist,
            album = album,
            uri = uri.toUri(),
            year = year,
            duration = duration
        )
    }
}