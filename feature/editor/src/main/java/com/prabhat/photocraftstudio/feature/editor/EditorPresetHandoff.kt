package com.prabhat.photocraftstudio.feature.editor

data class PendingPreset(
    val photoId: String,
    val filter: PhotoFilter,
)

object EditorPresetHandoff {

    private var pending: PendingPreset? = null

    fun set(photoId: String, filter: PhotoFilter) {
        pending = PendingPreset(photoId = photoId, filter = filter)
    }

    fun consumeFor(photoId: String): PhotoFilter? {
        val current = pending ?: return null
        pending = null
        return current.filter.takeIf { current.photoId == photoId }
    }
}
