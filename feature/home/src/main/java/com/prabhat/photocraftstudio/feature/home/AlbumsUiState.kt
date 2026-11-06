package com.prabhat.photocraftstudio.feature.home

import com.prabhat.photocraftstudio.core.data.AlbumSummary

data class AlbumsUiState(
    val albums: List<AlbumSummary> = emptyList(),
)
