package com.faysal.zenify.ui.util

import androidx.core.net.toUri
import com.faysal.zenify.domain.model.Audio


const val NOTIFICATION_ID = 1001
const val CHANNEL_ID = "music_channel"
const val ACTION_REPEAT = "com.faysal.zenify.ACTION_REPEAT"
const val ACTION_SHUFFLE = "com.faysal.zenify.ACTION_SHUFFLE"
const val ACTION_PLAY_PAUSE = "com.faysal.zenify.ACTION_PLAY_PAUSE"
const val ACTION_NEXT = "com.faysal.zenify.ACTION_NEXT"
const val ACTION_PREVIOUS = "com.faysal.zenify.ACTION_PREVIOUS"

const val EXTRA_AUDIO_URI = "AUDIO_URI"
const val EXTRA_AUDIO_TITLE = "AUDIO_TITLE"
const val EXTRA_AUDIO_ARTIST = "AUDIO_ARTIST"
const val EXTRA_AUDIO_ALBUM = "AUDIO_ALBUM"
const val EXTRA_AUDIO_DURATION = "AUDIO_DURATION"

const val PLAYBACK_STATE_DATASTORE = "playback_state"
const val PLAYLIST_DATASTORE = "playlist_data"



val sampleAudios = listOf(
    Audio(
        id = 1L,
        title = "Blinding Lights",
        artist = "The Weeknd",
        album = "After Hours",
        duration = 200000,
        year = "2026",
        uri = "content://media/external/audio/media/1".toUri()
    ),
    Audio(
        id = 2L,
        title = "Shape of You",
        artist = "Ed Sheeran",
        album = "Divide",
        duration = 240000,
        year = "2026",
        uri = "content://media/external/audio/media/2".toUri()
    ),
    Audio(
        id = 3L,
        title = "Someone Like You",
        artist = "Adele",
        album = "21",
        duration = 285000,
        year = "2026",
        uri = "content://media/external/audio/media/3".toUri()
    ),
    Audio(
        id = 4L,
        title = "Perfect",
        artist = "Ed Sheeran",
        album = "Divide",
        duration = 263000,
        year = "2026",
        uri = "content://media/external/audio/media/4".toUri()
    ),
    Audio(
        id = 5L,
        title = "Starboy",
        artist = "The Weeknd",
        album = "Starboy",
        duration = 230000,
        year = "2026",
        uri = "content://media/external/audio/media/5".toUri()
    ),
    Audio(
        id = 6L,
        title = "Rolling in the Deep",
        artist = "Adele",
        album = "21",
        duration = 228000,
        year = "2026",
        uri = "content://media/external/audio/media/6".toUri()
    ),
    Audio(
        id = 7L,
        title = "Levitating",
        artist = "Dua Lipa",
        album = "Future Nostalgia",
        duration = 203000,
        year = "2026",
        uri = "content://media/external/audio/media/7".toUri()
    ),
    Audio(
        id = 8L,
        title = "Bad Habits",
        artist = "Ed Sheeran",
        album = "=",
        duration = 231000,
        year = "2026",
        uri = "content://media/external/audio/media/8".toUri()
    )
)
