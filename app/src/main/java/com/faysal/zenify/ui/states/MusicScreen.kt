package com.faysal.zenify.ui.states

sealed class MusicScreen {
    object SongsList : MusicScreen()
    object AlbumList : MusicScreen()
    object ArtistList : MusicScreen()
    data class AlbumSongs(val album: String) : MusicScreen()
    data class ArtistSongs(val artist: String) : MusicScreen()
    object FolderList : MusicScreen()
    data class FolderSongs(val folderPath: String) : MusicScreen()

    override fun toString(): String = when (this) {
        is SongsList -> SongsList::class.simpleName!!
        is AlbumList -> AlbumList::class.simpleName!!
        is ArtistList -> ArtistList::class.simpleName!!
        is AlbumSongs -> "${AlbumSongs::class.simpleName}($album)"
        is ArtistSongs -> "${ArtistSongs::class.simpleName}($artist)"
        is FolderList -> FolderList::class.simpleName!!
        is FolderSongs -> "${FolderSongs::class.simpleName}($folderPath)"
    }

    companion object {
        fun fromString(value: String): MusicScreen? = when {
            value == SongsList::class.simpleName -> SongsList
            value == AlbumList::class.simpleName -> AlbumList
            value == ArtistList::class.simpleName -> ArtistList
            value == FolderList::class.simpleName -> FolderList
            value.startsWith("${AlbumSongs::class.simpleName}(") -> {
                val album = value.substringAfter("(").substringBeforeLast(")")
                AlbumSongs(album)
            }
            value.startsWith("${ArtistSongs::class.simpleName}(") -> {
                val artist = value.substringAfter("(").substringBeforeLast(")")
                ArtistSongs(artist)
            }
            value.startsWith("${FolderSongs::class.simpleName}(") -> {
                val path = value.substringAfter("(").substringBeforeLast(")")
                FolderSongs(path)
            }
            else -> null
        }
    }
}
