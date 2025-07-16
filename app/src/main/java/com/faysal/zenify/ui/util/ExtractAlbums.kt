package com.faysal.zenify.ui.util

import com.faysal.zenify.domain.model.Audio
import com.faysal.zenify.domain.model.UiTypes
import java.io.File

data class HeaderInfo(
    val hint: String = "",
    val subTitle: String = ""
)

fun extractHeaderInfo(
    filterKey: String,
    audios: List<Audio>,
    uiTypes: UiTypes
): HeaderInfo {
   return try {
        when (uiTypes) {
            UiTypes.Album -> {
                val firstItem = audios.firstOrNull { it.album == filterKey }
                val totalSongs = audios.filter { it.album == firstItem?.album }.size
                val hintLabel = if (totalSongs == 1) "Song" else "Songs"
                val hint = "Album • $totalSongs $hintLabel • ${firstItem?.year}"
                val artists = audios.filter { it.album == firstItem?.album }.distinctBy {
                    it.artist
                }.joinToString(", ") { it.artist }

                HeaderInfo(hint = hint, subTitle = artists)
            }

            UiTypes.Artist -> {
                val firstItem = audios.firstOrNull { it.artist == filterKey }
                val totalSongs = audios.filter { it.artist == firstItem?.artist }.size
                val hintLabel = if (totalSongs == 1) "Song" else "Songs"
                val hint = "Artist • $totalSongs $hintLabel"
                val albums = audios.filter { it.artist == firstItem?.artist }.distinctBy {
                    it.album
                }.joinToString(", ") { it.album }

                HeaderInfo(hint = hint, subTitle = albums)
            }

            UiTypes.Folder -> {

                val totalSongs = audios.filter { audio ->
                    audio.uri.path?.let { File(it).parentFile?.path == filterKey } ?: false
                }

                val hintLabel = if (totalSongs.size == 1) "Song" else "Songs"
                val hint = "Folder • $totalSongs $hintLabel"
                val artists = totalSongs.distinctBy {
                    it.artist
                }.joinToString(", ") { it.artist }

                HeaderInfo(hint = hint, subTitle = artists)
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
        HeaderInfo(hint = "", subTitle = "")
    }
}