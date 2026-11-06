package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftDivider
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIconButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftSlider
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftValueLabel
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors
import kotlin.math.roundToInt

@Composable
internal fun EditorToolScaffold(
    title: String,
    subtitle: String,
    badge: String,
    photo: PhotoItem?,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PhotoItem) -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PhotoCraftSpacing.sm,
                    end = PhotoCraftDimensions.screenPadding,
                    top = PhotoCraftSpacing.xs,
                    bottom = PhotoCraftSpacing.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PhotoCraftIconButton(
                onClick = onBack,
                contentDescription = "Back to editor",
                containerColor = Color.Transparent,
                bordered = false,
            ) {
                PhotoCraftIcon(glyph = PhotoCraftGlyph.Back)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = PhotoCraftSpacing.sm),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            PhotoCraftValueLabel(value = badge)
        }

        PhotoCraftDivider()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (photo == null) {
                EmptyToolState(onOpenGallery = onOpenGallery)
            } else {
                content(photo)
            }
        }
    }
}

@Composable
internal fun ToolPercentSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: () -> Unit = {},
) {
    PhotoCraftSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        valueLabel = "${(value.coerceIn(0f, 1f) * 100f).roundToInt()}%",
        onValueChangeFinished = onValueChangeFinished,
    )
}

@Composable
internal fun ToolToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(PhotoCraftSpacing.md))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PhotoCraftOnGold,
                checkedTrackColor = colors.gold,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.elevated,
                uncheckedBorderColor = colors.divider,
            ),
        )
    }
}

@Composable
private fun EmptyToolState(onOpenGallery: () -> Unit) {
    val colors = MaterialTheme.photoCraftColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PhotoCraftSpacing.xxl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            PhotoCraftIcon(
                glyph = PhotoCraftGlyph.Image,
                modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
                tint = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.lg))
            Text(
                text = "No photo selected",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
            Text(
                text = "Choose a photo from the gallery to open this workspace.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xl))
            PhotoCraftButton(onClick = onOpenGallery) {
                Text(text = "Open Gallery")
            }
        }
    }
}
