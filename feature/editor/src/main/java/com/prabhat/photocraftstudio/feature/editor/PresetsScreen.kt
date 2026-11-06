package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCard
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftDivider
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftSectionHeader
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftValueLabel
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PresetsRoute(
    onOpenGallery: () -> Unit,
    onApplyPreset: (PhotoItem, PhotoFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val photos by PhotoRepository.photos.collectAsStateWithLifecycle()
    val selectedPhoto by PhotoRepository.selectedPhoto.collectAsStateWithLifecycle()
    var previewPhotoId by rememberSaveable { mutableStateOf<String?>(null) }

    val previewPhoto = photos.firstOrNull { it.id == previewPhotoId }
        ?: photos.firstOrNull { it.id == selectedPhoto?.id }
        ?: photos.firstOrNull()

    PresetsScreen(
        photos = photos,
        previewPhoto = previewPhoto,
        onPreviewPhotoSelected = { previewPhotoId = it.id },
        onApplyPreset = { filter ->
            previewPhoto?.let { photo -> onApplyPreset(photo, filter) }
        },
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    )
}

@Composable
fun PresetsScreen(
    photos: List<PhotoItem>,
    previewPhoto: PhotoItem?,
    onPreviewPhotoSelected: (PhotoItem) -> Unit,
    onApplyPreset: (PhotoFilter) -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.surface, colors.background),
                ),
            ),
    ) {
        PresetsHeader(photoCount = photos.size)

        if (photos.isEmpty() || previewPhoto == null) {
            EmptyPresets(
                onOpenGallery = onOpenGallery,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 148.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = PhotoCraftDimensions.screenPadding,
                    end = PhotoCraftDimensions.screenPadding,
                    bottom = PhotoCraftSpacing.xxl,
                ),
                horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    PreviewHero(
                        photo = previewPhoto,
                        modifier = Modifier.padding(bottom = PhotoCraftSpacing.sm),
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    PhotoStrip(
                        photos = photos,
                        selectedPhotoId = previewPhoto.id,
                        onPreviewPhotoSelected = onPreviewPhotoSelected,
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(top = PhotoCraftSpacing.sm)) {
                        PhotoCraftSectionHeader(
                            title = "Signature looks",
                            subtitle = "Tap a look to open it in the editor with the preset applied.",
                        )
                        Spacer(modifier = Modifier.height(PhotoCraftSpacing.sm))
                        PhotoCraftDivider()
                        Spacer(modifier = Modifier.height(PhotoCraftSpacing.sm))
                    }
                }

                gridItems(items = PhotoFilter.entries, key = { it.name }) { filter ->
                    PresetCard(
                        photo = previewPhoto,
                        filter = filter,
                        onClick = { onApplyPreset(filter) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetsHeader(photoCount: Int) {
    val colors = MaterialTheme.photoCraftColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = PhotoCraftDimensions.screenPadding,
                end = PhotoCraftDimensions.screenPadding,
                top = PhotoCraftSpacing.md,
                bottom = PhotoCraftSpacing.lg,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "CURATED LOOKS",
                style = MaterialTheme.typography.labelSmall,
                color = colors.gold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xxs))
            Text(
                text = "Presets",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
            )
            Text(
                text = "$photoCount ${if (photoCount == 1) "photo" else "photos"} ready to style",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        PhotoCraftIcon(
            glyph = PhotoCraftGlyph.Presets,
            modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
            tint = colors.gold.copy(alpha = 0.75f),
        )
    }
}

@Composable
private fun PreviewHero(
    photo: PhotoItem,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    PhotoCraftCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(PhotoCraftCornerRadius.card),
        bordered = false,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.55f),
        ) {
            PhotoCraftAsyncImage(
                model = photo.imageModel,
                contentDescription = photo.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                maxDimension = 1024,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.82f),
                            ),
                        ),
                    ),
            )
            PhotoCraftValueLabel(
                value = "Preview photo",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(PhotoCraftSpacing.md),
                containerColor = Color.Black.copy(alpha = 0.55f),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(PhotoCraftSpacing.lg),
            ) {
                Text(
                    text = photo.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = photo.album,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f),
                )
            }
        }
    }
}

@Composable
private fun PhotoStrip(
    photos: List<PhotoItem>,
    selectedPhotoId: String,
    onPreviewPhotoSelected: (PhotoItem) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
    ) {
        lazyRowItems(items = photos, key = { it.id }) { photo ->
            val selected = photo.id == selectedPhotoId
            PhotoCraftCard(
                onClick = { onPreviewPhotoSelected(photo) },
                modifier = Modifier
                    .width(72.dp)
                    .aspectRatio(0.92f),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(PhotoCraftCornerRadius.md),
                bordered = selected,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PhotoCraftAsyncImage(
                        model = photo.imageModel,
                        contentDescription = photo.name,
                        modifier = Modifier.fillMaxSize(),
                        maxDimension = 256,
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    photo: PhotoItem,
    filter: PhotoFilter,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    PhotoCraftCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(PhotoCraftCornerRadius.card),
        bordered = false,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.82f),
        ) {
            PhotoCraftAsyncImage(
                model = photo.imageModel,
                contentDescription = "${filter.label} preset preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(
                    EditState(filter = filter).toComposeColorMatrix(),
                ),
                maxDimension = 512,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.86f),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(PhotoCraftSpacing.md),
            ) {
                Text(
                    text = filter.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = filter.description(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.68f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (filter == PhotoFilter.Original) {
                PhotoCraftValueLabel(
                    value = "Clean",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(PhotoCraftSpacing.sm),
                    containerColor = Color.Black.copy(alpha = 0.55f),
                )
            }
        }
    }
}

@Composable
private fun EmptyPresets(
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    Box(
        modifier = modifier.padding(PhotoCraftSpacing.xxl),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PhotoCraftIcon(
                glyph = PhotoCraftGlyph.Presets,
                modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
                tint = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.lg))
            Text(
                text = "No photos to style",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
            Text(
                text = "Add photos to the gallery, then return to preview the signature looks.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xl))
            PhotoCraftButton(onClick = onOpenGallery) {
                Text(text = "Open Gallery")
            }
        }
    }
}

private fun PhotoFilter.description(): String = when (this) {
    PhotoFilter.Original -> "True-to-life color"
    PhotoFilter.Mono -> "Sculpted black and white"
    PhotoFilter.Warm -> "Golden hour warmth"
    PhotoFilter.Cool -> "Cinematic blue shadows"
    PhotoFilter.Vintage -> "Faded editorial film"
}
