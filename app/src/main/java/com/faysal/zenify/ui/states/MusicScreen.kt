package com.faysal.zenify.ui.states

sealed class MusicScreen {
    object TrackList : MusicScreen()
    object AlbumList : MusicScreen()
    data class AlbumSongs(val album: String) : MusicScreen()
    data class ArtistSongs(val artist: String) : MusicScreen()
    object FolderList : MusicScreen()
    data class FolderSongs(val folderPath: String) : MusicScreen()


    companion object {
        fun fromString(value: String): MusicScreen? = when {
            value == TrackList::class.simpleName -> TrackList
            value == AlbumList::class.simpleName -> AlbumList
            value == FolderList::class.simpleName -> FolderList
            value.startsWith(AlbumSongs::class.simpleName ?: "") -> {
                val album = value.substringAfter("(").substringBefore(")")
                AlbumSongs(album)
            }
            value.startsWith(ArtistSongs::class.simpleName ?: "") -> {
                val artist = value.substringAfter("(").substringBefore(")")
                ArtistSongs(artist)
            }
            value.startsWith(FolderSongs::class.simpleName ?: "") -> {
                val folderPath = value.substringAfter("(").substringBefore(")")
                FolderSongs(folderPath)
            }
            else -> null
        }
    }

}
