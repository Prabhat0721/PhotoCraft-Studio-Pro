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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

private enum class MaskType(val label: String, val description: String) {
    Linear("Linear", "Blend an adjustment across a straight gradient."),
    Radial("Radial", "Build a soft spotlight around the subject."),
    Brush("Brush", "Paint a targeted selection by hand."),
}

@Composable
fun MaskingRoute(
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val photo by PhotoRepository.selectedPhoto.collectAsStateWithLifecycle()

    MaskingScreen(
        photo = photo,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    )
}

@Composable
fun MaskingScreen(
    photo: PhotoItem?,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTypeIndex by rememberSaveable { mutableIntStateOf(0) }
    var feather by rememberSaveable { mutableFloatStateOf(0.42f) }
    var opacity by rememberSaveable { mutableFloatStateOf(0.72f) }
    val selectedType = MaskType.entries.getOrElse(selectedTypeIndex) { MaskType.Linear }

    EditorToolScaffold(
        title = "Masking",
        subtitle = "Guide the viewer with selective light and color.",
        badge = "Mask",
        photo = photo,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    ) { currentPhoto ->
        MaskWorkspace(
            photo = currentPhoto,
            type = selectedType,
            feather = feather,
            opacity = opacity,
            onTypeSelected = { selectedTypeIndex = it.ordinal },
            onFeatherChange = { feather = it },
            onOpacityChange = { opacity = it },
            onReset = {
                selectedTypeIndex = MaskType.Linear.ordinal
                feather = 0.42f
                opacity = 0.72f
            },
        )
    }
}

@Composable
private fun MaskWorkspace(
    photo: PhotoItem,
    type: MaskType,
    feather: Float,
    opacity: Float,
    onTypeSelected: (MaskType) -> Unit,
    onFeatherChange: (Float) -> Unit,
    onOpacityChange: (Float) -> Unit,
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
                val maskColor = colors.gold
                val strength = opacity.coerceIn(0f, 1f)
                when (type) {
                    MaskType.Linear -> {
                        val softEdge = 0.22f + feather * 0.46f
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    maskColor.copy(alpha = strength * 0.52f),
                                    Color.Transparent,
                                ),
                                startY = size.height * (0.05f),
                                endY = size.height * (softEdge + 0.38f),
                            ),
                            size = size,
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.55f),
                            start = Offset(size.width * 0.12f, size.height * 0.24f),
                            end = Offset(size.width * 0.88f, size.height * 0.24f),
                            strokeWidth = 1.2.dp.toPx(),
                        )
                    }

                    MaskType.Radial -> {
                        val radius = size.minDimension * (0.22f + feather * 0.34f)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    maskColor.copy(alpha = strength * 0.5f),
                                    maskColor.copy(alpha = strength * 0.16f),
                                    Color.Transparent,
                                ),
                                center = center,
                                radius = radius,
                            ),
                            radius = radius,
                            center = center,
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.4f),
                            radius = radius,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 1.dp.toPx(),
                            ),
                        )
                    }

                    MaskType.Brush -> {
                        val radius = size.minDimension * (0.08f + feather * 0.08f)
                        repeat(6) { index ->
                            val progress = index / 5f
                            drawCircle(
                                color = maskColor.copy(alpha = strength * 0.3f),
                                radius = radius * (0.72f + progress * 0.4f),
                                center = Offset(
                                    size.width * (0.28f + progress * 0.44f),
                                    size.height * (0.6f - progress * 0.22f),
                                ),
                            )
                        }
                    }
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
            PhotoCraftCategoryTabs(
                categories = MaskType.entries.map { it.label },
                selectedCategory = type.label,
                onCategorySelected = { label ->
                    MaskType.entries.firstOrNull { it.label == label }?.let(onTypeSelected)
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            )

            Text(
                text = type.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )

            ToolPercentSlider(
                label = "Feather",
                value = feather,
                onValueChange = onFeatherChange,
            )
            ToolPercentSlider(
                label = "Opacity",
                value = opacity,
                onValueChange = onOpacityChange,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${type.label} selection",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                PhotoCraftButton(
                    onClick = onReset,
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Reset mask")
                }
            }
        }
    }
}
