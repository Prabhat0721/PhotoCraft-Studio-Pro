package com.prabhat.photocraftstudio.feature.home

import com.prabhat.photocraftstudio.core.data.PhotoItem

data class HomeUiState(
    val photos: List<PhotoItem> = emptyList(),
    val filters: List<String> = listOf(ALL_FILTER, FAVORITES_FILTER),
    val selectedFilter: String = ALL_FILTER,
    val query: String = "",
    val isImporting: Boolean = false,
    val message: String? = null,
) {
    companion object {
        const val ALL_FILTER = "All"
        const val FAVORITES_FILTER = "Favorites"
    }
}
