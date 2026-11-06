package com.prabhat.photocraftstudio.feature.home

import androidx.lifecycle.ViewModel
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlbumsViewModel : ViewModel() {

    val uiState: Flow<AlbumsUiState> = PhotoRepository.photos.map { photos ->
        AlbumsUiState(albums = PhotoRepository.albumSummaries(photos))
    }
}
