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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButtonVariant
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCategoryTabs
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

private enum class CropAspect(val label: String, val ratio: Float?) {
    Original("Original", null),
    Square("1:1", 1f),
    Portrait("4:5", 4f / 5f),
    Landscape("3:2", 3f / 2f),
    Wide("16:9", 16f / 9f),
}

@Composable
fun CropRoute(
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val photo by PhotoRepository.selectedPhoto.collectAsStateWithLifecycle()

    CropScreen(
        photo = photo,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    )
}

@Composable
fun CropScreen(
    photo: PhotoItem?,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedAspectIndex by rememberSaveable { mutableIntStateOf(0) }
    var showGrid by rememberSaveable { mutableStateOf(true) }
    val selectedAspect = CropAspect.entries.getOrElse(selectedAspectIndex) { CropAspect.Original }

    EditorToolScaffold(
        title = "Crop & straighten",
        subtitle = "Compose the frame and preview your final aspect ratio.",
        badge = "Composition",
        photo = photo,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    ) { currentPhoto ->
        CropWorkspace(
            photo = currentPhoto,
            selectedAspect = selectedAspect,
            showGrid = showGrid,
            onAspectSelected = { selectedAspectIndex = it.ordinal },
            onShowGridChange = { showGrid = it },
            onReset = {
                selectedAspectIndex = CropAspect.Original.ordinal
                showGrid = true
            },
        )
    }
}

@Composable
private fun CropWorkspace(
    photo: PhotoItem,
    selectedAspect: CropAspect,
    showGrid: Boolean,
    onAspectSelected: (CropAspect) -> Unit,
    onShowGridChange: (Boolean) -> Unit,
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

            val availableWidth = maxWidth * 0.86f
            val availableHeight = maxHeight * 0.86f
            val targetRatio = selectedAspect.ratio ?: (maxWidth.value / maxHeight.value)
            val widthFromHeight = availableHeight * targetRatio
            val frameWidth = if (widthFromHeight <= availableWidth) {
                widthFromHeight
            } else {
                availableWidth
            }
            val frameHeight = if (widthFromHeight <= availableWidth) {
                availableHeight
            } else {
                availableWidth / targetRatio
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val frameWidthPx = frameWidth.toPx()
                val frameHeightPx = frameHeight.toPx()
                val left = (size.width - frameWidthPx) / 2f
                val top = (size.height - frameHeightPx) / 2f
                val scrim = Color.Black.copy(alpha = 0.58f)

                drawRect(
                    color = scrim,
                    topLeft = Offset.Zero,
                    size = Size(size.width, top),
                )
                drawRect(
                    color = scrim,
                    topLeft = Offset(0f, top + frameHeightPx),
                    size = Size(size.width, size.height - top - frameHeightPx),
                )
                drawRect(
                    color = scrim,
                    topLeft = Offset(0f, top),
                    size = Size(left, frameHeightPx),
                )
                drawRect(
                    color = scrim,
                    topLeft = Offset(left + frameWidthPx, top),
                    size = Size(size.width - left - frameWidthPx, frameHeightPx),
                )
                drawRect(
                    color = colors.gold,
                    topLeft = Offset(left, top),
                    size = Size(frameWidthPx, frameHeightPx),
                    style = Stroke(width = 1.6.dp.toPx()),
                )

                if (showGrid) {
                    val lineColor = Color.White.copy(alpha = 0.34f)
                    for (index in 1..2) {
                        val x = left + frameWidthPx * index / 3f
                        val y = top + frameHeightPx * index / 3f
                        drawLine(
                            color = lineColor,
                            start = Offset(x, top),
                            end = Offset(x, top + frameHeightPx),
                            strokeWidth = 1.dp.toPx(),
                        )
                        drawLine(
                            color = lineColor,
                            start = Offset(left, y),
                            end = Offset(left + frameWidthPx, y),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                }

                val handle = 14.dp.toPx()
                val cornerColor = colors.gold
                listOf(
                    Offset(left, top) to Offset(1f, 1f),
                    Offset(left + frameWidthPx, top) to Offset(-1f, 1f),
                    Offset(left, top + frameHeightPx) to Offset(1f, -1f),
                    Offset(left + frameWidthPx, top + frameHeightPx) to Offset(-1f, -1f),
                ).forEach { (corner, direction) ->
                    drawLine(
                        color = cornerColor,
                        start = corner,
                        end = Offset(corner.x + handle * direction.x, corner.y),
                        strokeWidth = 2.4.dp.toPx(),
                    )
                    drawLine(
                        color = cornerColor,
                        start = corner,
                        end = Offset(corner.x, corner.y + handle * direction.y),
                        strokeWidth = 2.4.dp.toPx(),
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
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
        ) {
            PhotoCraftCategoryTabs(
                categories = CropAspect.entries.map { it.label },
                selectedCategory = selectedAspect.label,
                onCategorySelected = { label ->
                    CropAspect.entries.firstOrNull { it.label == label }?.let(onAspectSelected)
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            )

            ToolToggleRow(
                label = "Rule of thirds",
                description = "Show composition guides inside the crop frame.",
                checked = showGrid,
                onCheckedChange = onShowGridChange,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
            ) {
                Text(
                    text = "${selectedAspect.label} frame",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                PhotoCraftButton(
                    onClick = onReset,
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Reset frame")
                }
            }
        }
    }
}
