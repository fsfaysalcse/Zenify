package com.faysal.zenify.data.service

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@OptIn(UnstableApi::class)
class MediaLibraryCallback : MediaLibraryService.MediaLibrarySession.Callback {

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: List<MediaItem>
    ): ListenableFuture<List<MediaItem>> {
        try {
            val resolvedItems = mediaItems.map { item ->
                MediaItem.Builder()
                    .setUri(item.requestMetadata.mediaUri)
                    .setMediaId(item.mediaId)
                    .setMediaMetadata(item.mediaMetadata)
                    .build()
            }
            return Futures.immediateFuture(resolvedItems)
        } catch (e: Exception) {
            e.printStackTrace()
            return Futures.immediateFuture(emptyList())
        }
    }
}