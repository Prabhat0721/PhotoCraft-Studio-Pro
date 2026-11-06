package com.prabhat.photocraftstudio.feature.home

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import com.prabhat.photocraftstudio.core.data.createImportedPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(HomeUiState.ALL_FILTER)
    private val isImporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: Flow<HomeUiState> = combine(
        PhotoRepository.photos,
        query,
        selectedFilter,
        isImporting,
        message,
    ) { photos, currentQuery, currentFilter, importing, currentMessage ->
        buildHomeUiState(
            allPhotos = photos,
            query = currentQuery,
            selectedFilter = currentFilter,
            isImporting = importing,
            message = currentMessage,
        )
    }

    fun onSearchQueryChange(value: String) {
        query.value = value
    }

    fun setFilter(filter: String) {
        selectedFilter.value = filter
    }

    fun toggleFavorite(photoId: String) {
        PhotoRepository.toggleFavorite(photoId)
    }

    fun importPhotos(contentResolver: ContentResolver, uris: List<Uri>) {
        if (uris.isEmpty() || isImporting.value) return
        viewModelScope.launch {
            isImporting.value = true
            val imported = withContext(Dispatchers.IO) {
                uris.mapNotNull { uri ->
                    runCatching { createImportedPhoto(contentResolver, uri) }.getOrNull()
                }
            }
            if (imported.isNotEmpty()) {
                PhotoRepository.addImported(imported)
                query.value = ""
                selectedFilter.value = HomeUiState.ALL_FILTER
                message.value = "Imported ${imported.size} ${if (imported.size == 1) "photo" else "photos"}."
            } else {
                message.value = "Could not import the selected photos."
            }
            isImporting.value = false
        }
    }

    fun consumeMessage() {
        message.value = null
    }
}

internal fun buildHomeUiState(
    allPhotos: List<PhotoItem>,
    query: String,
    selectedFilter: String,
    isImporting: Boolean = false,
    message: String? = null,
): HomeUiState {
    val filters = listOf(HomeUiState.ALL_FILTER, HomeUiState.FAVORITES_FILTER) +
        allPhotos.map { it.album }.distinct().sorted()
    val effectiveFilter = selectedFilter.takeIf { it in filters } ?: HomeUiState.ALL_FILTER
    val normalizedQuery = query.trim()

    val filtered = allPhotos
        .filter { photo ->
            val matchesFilter = when (effectiveFilter) {
                HomeUiState.ALL_FILTER -> true
                HomeUiState.FAVORITES_FILTER -> photo.isFavorite
                else -> photo.album == effectiveFilter
            }
            val matchesQuery = normalizedQuery.isEmpty() ||
                photo.name.contains(normalizedQuery, ignoreCase = true) ||
                photo.album.contains(normalizedQuery, ignoreCase = true)
            matchesFilter && matchesQuery
        }
        .sortedByDescending { it.dateAddedMillis }

    return HomeUiState(
        photos = filtered,
        filters = filters,
        selectedFilter = effectiveFilter,
        query = query,
        isImporting = isImporting,
        message = message,
    )
}
