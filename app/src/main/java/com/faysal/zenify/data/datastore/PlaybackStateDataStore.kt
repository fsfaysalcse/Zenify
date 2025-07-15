package com.faysal.zenify.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "playback_state")

class PlaybackStateManager(private val context: Context) {

    private val currentIndexKey = intPreferencesKey("current_index")
    private val currentPositionKey = longPreferencesKey("current_position")
    private val isRepeatEnabledKey = booleanPreferencesKey("is_repeat_enabled")
    private val isShuffleEnabledKey = booleanPreferencesKey("is_shuffle_enabled")
    private val isPlayingKey = booleanPreferencesKey("is_playing")

    val currentIndex: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[currentIndexKey] ?: -1 }

    val currentPosition: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[currentPositionKey] ?: 0L }

    val isRepeatEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[isRepeatEnabledKey] ?: false }

    val isShuffleEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[isShuffleEnabledKey] ?: false }

    val isPlaying: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[isPlayingKey] ?: false }

    suspend fun saveCurrentIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[currentIndexKey] = index
        }
    }

    suspend fun saveCurrentPosition(position: Long) {
        context.dataStore.edit { preferences ->
            preferences[currentPositionKey] = position
        }
    }

    suspend fun saveRepeatState(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isRepeatEnabledKey] = enabled
        }
    }

    suspend fun saveShuffleState(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isShuffleEnabledKey] = enabled
        }
    }

    suspend fun savePlayingState(playing: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isPlayingKey] = playing
        }
    }

    suspend fun clearState() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}