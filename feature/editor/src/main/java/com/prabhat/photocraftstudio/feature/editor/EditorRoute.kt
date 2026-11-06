package com.prabhat.photocraftstudio.feature.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhat.photocraftstudio.core.data.AppSettingsRepository
import com.prabhat.photocraftstudio.core.data.PhotoItem
import kotlinx.coroutines.launch

@Composable
fun EditorRoute(
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenCrop: () -> Unit,
    onOpenMasking: () -> Unit,
    onOpenPortrait: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = EditorUiState())
    LaunchedEffect(uiState.photo?.id) {
        viewModel.onPhotoChanged(uiState.photo?.id)
        uiState.photo?.let { photo -> viewModel.applyPendingPreset(photo.id) }
    }

    val context = LocalContext.current
    val settingsRepository = remember(context) { AppSettingsRepository.get(context) }
    val settings by settingsRepository.settings.collectAsStateWithLifecycle()
    val view = LocalView.current
    DisposableEffect(view, settings.keepScreenOnInEditor) {
        val previous = view.keepScreenOn
        view.keepScreenOn = settings.keepScreenOnInEditor
        onDispose { view.keepScreenOn = previous }
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingExport by remember { mutableStateOf<Pair<PhotoItem, EditState>?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg"),
    ) { uri ->
        val pending = pendingExport
        pendingExport = null
        if (uri != null && pending != null) {
            scope.launch {
                val result = PhotoExporter.export(context, pending.first, pending.second, uri)
                snackbarHostState.showSnackbar(
                    result.fold(
                        onSuccess = { "Exported ${pending.first.name}.jpg" },
                        onFailure = { failure ->
                            "Export failed. ${failure.message ?: "Try another destination."}"
                        },
                    ),
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        EditorScreen(
            uiState = uiState,
            onBack = onBack,
            onOpenGallery = onOpenGallery,
            onExport = {
                val photo = uiState.photo
                if (photo != null) {
                    pendingExport = photo to uiState.edit
                    exportLauncher.launch("${photo.name.replace(' ', '_')}_edited.jpg")
                }
            },
            onUndo = viewModel::undo,
            onRedo = viewModel::redo,
            onReset = viewModel::reset,
            onFilterSelected = viewModel::setFilter,
            onLightChange = viewModel::updateLight,
            onColorChange = viewModel::updateColor,
            onHslChange = viewModel::updateHsl,
            onGradingWheelChange = viewModel::updateGradingWheel,
            onGradingLuminanceChange = viewModel::updateGradingLuminance,
            onEffectChange = viewModel::updateEffect,
            onDetailChange = viewModel::updateDetail,
            onGestureFinished = viewModel::finishGesture,
            onRotate = viewModel::rotateClockwise,
            onFlip = viewModel::toggleFlip,
            onOpenCrop = onOpenCrop,
            onOpenMasking = onOpenMasking,
            onOpenPortrait = onOpenPortrait,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }
}
