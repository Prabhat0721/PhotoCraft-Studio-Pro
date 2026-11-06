package com.prabhat.photocraftstudio.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object PhotoRepository {

    private val _photos = MutableStateFlow(samplePhotos())
    val photos: StateFlow<List<PhotoItem>> = _photos.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<PhotoItem?>(null)
    val selectedPhoto: StateFlow<PhotoItem?> = _selectedPhoto.asStateFlow()

    fun selectPhoto(photo: PhotoItem?) {
        _selectedPhoto.value = photo?.let { current -> _photos.value.firstOrNull { it.id == current.id } ?: current }
    }

    fun findById(id: String): PhotoItem? = _photos.value.firstOrNull { it.id == id }

    fun addImported(photos: List<PhotoItem>) {
        if (photos.isEmpty()) return
        _photos.update { existing -> photos + existing }
    }

    fun toggleFavorite(id: String) {
        _photos.update { photos ->
            photos.map { photo ->
                if (photo.id == id) photo.copy(isFavorite = !photo.isFavorite) else photo
            }
        }
        _selectedPhoto.update { selected ->
            selected?.takeIf { it.id == id }?.let { current ->
                _photos.value.firstOrNull { it.id == current.id } ?: current
            } ?: selected
        }
    }

    fun albumSummaries(): List<AlbumSummary> = albumSummaries(_photos.value)

    fun albumSummaries(photos: List<PhotoItem>): List<AlbumSummary> {
        val favorites = photos.filter { it.isFavorite }
        val all = AlbumSummary(
            filter = "All",
            title = "All Photos",
            count = photos.size,
            coverPhoto = photos.firstOrNull(),
        )
        val favoriteAlbum = AlbumSummary(
            filter = "Favorites",
            title = "Favorites",
            count = favorites.size,
            coverPhoto = favorites.firstOrNull(),
        )
        val albums = photos
            .groupBy { it.album }
            .toSortedMap()
            .map { (album, albumPhotos) ->
                AlbumSummary(
                    filter = album,
                    title = album,
                    count = albumPhotos.size,
                    coverPhoto = albumPhotos.firstOrNull(),
                )
            }
        return listOf(all, favoriteAlbum) + albums
    }
}
