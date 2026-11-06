package com.prabhat.photocraftstudio.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AlbumsRoute(
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = AlbumsUiState())
    AlbumsScreen(
        uiState = uiState,
        onAlbumClick = onAlbumClick,
        modifier = modifier,
    )
}
