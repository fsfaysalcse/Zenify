package com.faysal.zenify.ui.util


import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ImageColors(
    val vibrant: Color = Color.Gray,
    val muted: Color = Color.DarkGray,
    val dominant: Color = Color.Black
)

/**
 * Extracts dominant colors from an ImageBitmap using Palette API.
 * @param imageBitmap The ImageBitmap to extract colors from.
 * @return ImageColors containing vibrant, muted, and dominant colors.
 */
suspend fun extractColorsFromImage(imageBitmap: androidx.compose.ui.graphics.ImageBitmap): ImageColors {
    return withContext(Dispatchers.Default) {
        try {
            val bitmap = imageBitmap.asAndroidBitmap()
            val palette = Palette.from(bitmap).generate()

            ImageColors(
                vibrant = palette.vibrantSwatch?.rgb?.let { Color(it) } ?: Color.Gray,
                muted = palette.mutedSwatch?.rgb?.let { Color(it) } ?: Color.DarkGray,
                dominant = palette.dominantSwatch?.rgb?.let { Color(it) } ?: Color.Black
            )
        } catch (e: Exception) {
            // Fallback in case of errors
            ImageColors()
        }
    }
}

/**
 * Loads an image from a URI and extracts its colors.
 * @param context The Android context.
 * @param uri The URI of the image.
 * @return ImageColors containing extracted colors or default values if loading fails.
 */
suspend fun extractColorsFromUri(context: Context, uri: String?): ImageColors {
    if (uri == null) return ImageColors()

    return withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false) // Disable hardware acceleration for Palette
                .build()

            val bitmap = context.imageLoader.execute(request).image?.toBitmap()
                ?: return@withContext ImageColors()

            val palette = Palette.from(bitmap).generate()

            ImageColors(
                vibrant = palette.vibrantSwatch?.rgb?.let { Color(it) } ?: Color.Gray,
                muted = palette.mutedSwatch?.rgb?.let { Color(it) } ?: Color.DarkGray,
                dominant = palette.dominantSwatch?.rgb?.let { Color(it) } ?: Color.Black
            )
        } catch (e: Exception) {
            ImageColors()
        }
    }
}