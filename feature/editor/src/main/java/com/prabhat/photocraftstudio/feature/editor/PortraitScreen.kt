package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButtonVariant
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PortraitRoute(
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = EditorUiState())

    PortraitScreen(
        photo = uiState.photo,
        faceLighting = uiState.edit.portrait.faceLighting,
        skinSmoothing = uiState.edit.portrait.skinSmoothing,
        eyeClarity = uiState.edit.portrait.eyeClarity,
        backgroundDepth = uiState.edit.portrait.backgroundDepth,
        onFaceLightingChange = { enabled -> viewModel.updatePortrait { it.copy(faceLighting = enabled) } },
        onSkinSmoothingChange = { value -> viewModel.updatePortrait { it.copy(skinSmoothing = value) } },
        onEyeClarityChange = { value -> viewModel.updatePortrait { it.copy(eyeClarity = value) } },
        onBackgroundDepthChange = { value -> viewModel.updatePortrait { it.copy(backgroundDepth = value) } },
        onReset = { viewModel.reset() },
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    )
}

@Composable
fun PortraitScreen(
    photo: PhotoItem?,
    faceLighting: Boolean,
    skinSmoothing: Float,
    eyeClarity: Float,
    backgroundDepth: Float,
    onFaceLightingChange: (Boolean) -> Unit,
    onSkinSmoothingChange: (Float) -> Unit,
    onEyeClarityChange: (Float) -> Unit,
    onBackgroundDepthChange: (Float) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditorToolScaffold(
        title = "Portrait",
        subtitle = "Sculpt flattering light and background separation.",
        badge = "Portrait",
        photo = photo,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    ) { currentPhoto ->
        PortraitWorkspace(
            photo = currentPhoto,
            faceLighting = faceLighting,
            skinSmoothing = skinSmoothing,
            eyeClarity = eyeClarity,
            backgroundDepth = backgroundDepth,
            onFaceLightingChange = onFaceLightingChange,
            onSkinSmoothingChange = onSkinSmoothingChange,
            onEyeClarityChange = onEyeClarityChange,
            onBackgroundDepthChange = onBackgroundDepthChange,
            onReset = onReset,
        )
    }
}

@Composable
private fun PortraitWorkspace(
    photo: PhotoItem,
    faceLighting: Boolean,
    skinSmoothing: Float,
    eyeClarity: Float,
    backgroundDepth: Float,
    onFaceLightingChange: (Boolean) -> Unit,
    onSkinSmoothingChange: (Float) -> Unit,
    onEyeClarityChange: (Float) -> Unit,
    onBackgroundDepthChange: (Float) -> Unit,
    onReset: () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = PhotoCraftDimensions.screenPadding,
                    vertical = PhotoCraftSpacing.sm,
                )
                .clip(RoundedCornerShape(PhotoCraftCornerRadius.panel))
                .background(Color.Black),
        ) {
            PhotoCraftAsyncImage(
                model = photo.imageModel,
                contentDescription = photo.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                maxDimension = 1600,
                containerColor = Color.Black,
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.08f + backgroundDepth * 0.34f),
                        ),
                        center = center,
                        radius = size.minDimension * 0.72f,
                    ),
                    radius = size.minDimension * 0.72f,
                    center = center,
                )
                if (faceLighting) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                            ),
                            center = center,
                            radius = size.minDimension * 0.32f,
                        ),
                        radius = size.minDimension * 0.32f,
                        center = center,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PhotoCraftDimensions.screenPadding,
                    vertical = PhotoCraftSpacing.sm,
                ),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
        ) {
            ToolToggleRow(
                label = "Face lighting",
                description = "Open shadows and add a soft glow around the subject.",
                checked = faceLighting,
                onCheckedChange = onFaceLightingChange,
            )
            ToolPercentSlider(
                label = "Skin smoothing",
                value = skinSmoothing,
                onValueChange = onSkinSmoothingChange,
            )
            ToolPercentSlider(
                label = "Eye clarity",
                value = eyeClarity,
                onValueChange = onEyeClarityChange,
            )
            ToolPercentSlider(
                label = "Background depth",
                value = backgroundDepth,
                onValueChange = onBackgroundDepthChange,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Portrait study",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                PhotoCraftButton(
                    onClick = onReset,
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Reset portrait")
                }
            }
        }
    }
}
