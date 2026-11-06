package com.prabhat.photocraftstudio.core.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.util.UUID

fun createImportedPhoto(
    contentResolver: ContentResolver,
    uri: Uri,
    album: String = "Imported",
): PhotoItem {
    val displayName = runCatching {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }.getOrNull()

    val name = displayName
        ?.substringBeforeLast('.')
        ?.takeIf { it.isNotBlank() }
        ?: "Imported photo"

    return PhotoItem(
        id = "imported_${UUID.randomUUID()}",
        name = name,
        source = PhotoSource.ContentUri(uri),
        album = album,
        dateAddedMillis = System.currentTimeMillis(),
    )
}
