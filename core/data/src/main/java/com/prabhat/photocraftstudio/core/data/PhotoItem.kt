package com.prabhat.photocraftstudio.core.data

import android.net.Uri
import androidx.annotation.DrawableRes

sealed interface PhotoSource {
    data class Resource(@DrawableRes val drawableId: Int) : PhotoSource
    data class ContentUri(val uri: Uri) : PhotoSource
}

data class PhotoItem(
    val id: String,
    val name: String,
    val source: PhotoSource,
    val album: String,
    val dateAddedMillis: Long,
    val isFavorite: Boolean = false,
) {
    val imageModel: Any
        get() = when (source) {
            is PhotoSource.Resource -> source.drawableId
            is PhotoSource.ContentUri -> source.uri
        }
}

data class AlbumSummary(
    val filter: String,
    val title: String,
    val count: Int,
    val coverPhoto: PhotoItem?,
)
