package com.prabhat.photocraftstudio.feature.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhat.photocraftstudio.core.data.AppSettingsRepository
import com.prabhat.photocraftstudio.core.data.PhotoItem

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    initialFilter: String = HomeUiState.ALL_FILTER,
    onPhotoClick: (PhotoItem) -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = HomeUiState())
    LaunchedEffect(initialFilter) {
        viewModel.setFilter(initialFilter)
    }

    val context = LocalContext.current
    val settingsRepository = remember(context) { AppSettingsRepository.get(context) }
    val settings by settingsRepository.settings.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(20),
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importPhotos(context.contentResolver, uris)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { currentMessage ->
            snackbarHostState.showSnackbar(currentMessage)
            viewModel.consumeMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeScreen(
            uiState = uiState,
            galleryDensity = settings.galleryDensity,
            showPhotoLabels = settings.showPhotoLabels,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onFilterSelected = viewModel::setFilter,
            onToggleFavorite = viewModel::toggleFavorite,
            onPhotoClick = onPhotoClick,
            onImport = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }
}
