package com.prabhat.photocraftstudio.feature.home

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prabhat.photocraftstudio.core.data.GalleryDensity
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCard
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCategoryTabs
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIconButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftSearchField
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    galleryDensity: GalleryDensity = GalleryDensity.Comfortable,
    showPhotoLabels: Boolean = true,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPhotoClick: (PhotoItem) -> Unit,
    onImport: () -> Unit,
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
        GalleryHeader(
            photoCount = uiState.photos.size,
            isImporting = uiState.isImporting,
            onImport = onImport,
        )

        PhotoCraftSearchField(
            value = uiState.query,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PhotoCraftDimensions.screenPadding),
        )

        Spacer(modifier = Modifier.height(PhotoCraftSpacing.md))

        PhotoCraftCategoryTabs(
            categories = uiState.filters,
            selectedCategory = uiState.selectedFilter,
            onCategorySelected = onFilterSelected,
        )

        if (uiState.photos.isEmpty()) {
            EmptyGallery(
                isFiltering = uiState.query.isNotBlank() ||
                    uiState.selectedFilter != HomeUiState.ALL_FILTER,
                onImport = onImport,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = galleryDensity.minTileSizeDp.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = PhotoCraftDimensions.screenPadding,
                    end = PhotoCraftDimensions.screenPadding,
                    top = PhotoCraftSpacing.sm,
                    bottom = PhotoCraftSpacing.xxl,
                ),
                horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
            ) {
                items(items = uiState.photos, key = { it.id }) { photo ->
                    PhotoTile(
                        photo = photo,
                        showLabels = showPhotoLabels,
                        onClick = { onPhotoClick(photo) },
                        onToggleFavorite = { onToggleFavorite(photo.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryHeader(
    photoCount: Int,
    isImporting: Boolean,
    onImport: () -> Unit,
) {
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
                text = "PHOTOCRAFT STUDIO",
                style = MaterialTheme.typography.labelSmall,
                color = colors.gold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xxs))
            Text(
                text = "Gallery",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
            )
            Text(
                text = "$photoCount ${if (photoCount == 1) "photo" else "photos"}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        PhotoCraftIconButton(
            onClick = onImport,
            enabled = !isImporting,
            contentDescription = "Import photos",
            containerColor = colors.gold,
            contentColor = PhotoCraftOnGold,
            bordered = false,
        ) {
            if (isImporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PhotoCraftOnGold,
                    strokeWidth = 2.dp,
                )
            } else {
                PhotoCraftIcon(glyph = PhotoCraftGlyph.Plus)
            }
        }
    }
}

@Composable
private fun PhotoTile(
    photo: PhotoItem,
    showLabels: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    PhotoCraftCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(PhotoCraftCornerRadius.card),
        bordered = false,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PhotoCraftAsyncImage(
                model = photo.imageModel,
                contentDescription = photo.name,
                modifier = Modifier.fillMaxSize(),
                maxDimension = 512,
            )
            if (showLabels) {
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
            }
            PhotoCraftIconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(PhotoCraftSpacing.xs),
                contentDescription = if (photo.isFavorite) {
                    "Remove ${photo.name} from favorites"
                } else {
                    "Add ${photo.name} to favorites"
                },
                containerColor = Color.Black.copy(alpha = 0.42f),
                contentColor = if (photo.isFavorite) colors.gold else Color.White,
                bordered = false,
            ) {
                PhotoCraftIcon(
                    glyph = if (photo.isFavorite) {
                        PhotoCraftGlyph.HeartFilled
                    } else {
                        PhotoCraftGlyph.Heart
                    },
                )
            }
            if (showLabels) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(PhotoCraftSpacing.md),
                ) {
                    Text(
                        text = photo.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = photo.album,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGallery(
    isFiltering: Boolean,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    Box(
        modifier = modifier.padding(PhotoCraftSpacing.xxl),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PhotoCraftIcon(
                glyph = PhotoCraftGlyph.Image,
                modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
                tint = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.lg))
            Text(
                text = if (isFiltering) "No photos found" else "Your gallery is empty",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
            Text(
                text = if (isFiltering) {
                    "Try a different search or album."
                } else {
                    "Import photos to build your studio library."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xl))
            PhotoCraftButton(onClick = onImport) {
                PhotoCraftIcon(glyph = PhotoCraftGlyph.Plus)
                Spacer(modifier = Modifier.size(PhotoCraftSpacing.sm))
                Text(text = "Import photos")
            }
        }
    }
}
