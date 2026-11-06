package com.prabhat.photocraftstudio.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prabhat.photocraftstudio.core.data.AlbumSummary
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCard
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun AlbumsScreen(
    uiState: AlbumsUiState,
    onAlbumClick: (String) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PhotoCraftDimensions.screenPadding,
                    end = PhotoCraftDimensions.screenPadding,
                    top = PhotoCraftSpacing.md,
                    bottom = PhotoCraftSpacing.lg,
                ),
        ) {
            Text(
                text = "COLLECTIONS",
                style = MaterialTheme.typography.labelSmall,
                color = colors.gold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xxs))
            Text(
                text = "Albums",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
            )
            Text(
                text = "${uiState.albums.size} " +
                    if (uiState.albums.size == 1) "collection" else "collections",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = PhotoCraftDimensions.screenPadding,
                end = PhotoCraftDimensions.screenPadding,
                bottom = PhotoCraftSpacing.xxl,
            ),
            horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
        ) {
            items(items = uiState.albums, key = { it.filter }) { album ->
                AlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album.filter) },
                )
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: AlbumSummary,
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
                .aspectRatio(1.04f),
        ) {
            val coverPhoto = album.coverPhoto
            if (coverPhoto != null) {
                PhotoCraftAsyncImage(
                    model = coverPhoto.imageModel,
                    contentDescription = album.title,
                    modifier = Modifier.fillMaxSize(),
                    maxDimension = 512,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.elevated),
                    contentAlignment = Alignment.Center,
                ) {
                    PhotoCraftIcon(
                        glyph = PhotoCraftGlyph.Albums,
                        modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
                        tint = colors.textSecondary,
                    )
                }
            }

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
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${album.count} ${if (album.count == 1) "photo" else "photos"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f),
                )
            }
        }
    }
}
