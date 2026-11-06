package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButtonVariant
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCard
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
internal fun ToolsPanel(
    edit: EditState,
    onRotate: () -> Unit,
    onFlip: () -> Unit,
    onReset: () -> Unit,
    onOpenCrop: () -> Unit,
    onOpenMasking: () -> Unit,
    onOpenPortrait: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    EditorPanelContainer(
        title = "Tools",
        subtitle = "Composition, selective edits and portrait refinements.",
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PhotoCraftDimensions.screenPadding),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm)) {
                PhotoCraftButton(
                    onClick = onRotate,
                    modifier = Modifier.weight(1f),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    PhotoCraftIcon(glyph = PhotoCraftGlyph.Crop)
                    Spacer(modifier = Modifier.width(PhotoCraftSpacing.sm))
                    Text(text = "Rotate")
                }
                PhotoCraftButton(
                    onClick = onFlip,
                    modifier = Modifier.weight(1f),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    PhotoCraftIcon(glyph = PhotoCraftGlyph.Flip)
                    Spacer(modifier = Modifier.width(PhotoCraftSpacing.sm))
                    Text(text = "Flip")
                }
                PhotoCraftButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Reset")
                }
            }

            ToolLinkCard(
                glyph = PhotoCraftGlyph.Crop,
                title = "Crop & straighten",
                subtitle = "Frame the composition and lock an aspect ratio.",
                onClick = onOpenCrop,
            )
            ToolLinkCard(
                glyph = PhotoCraftGlyph.Mask,
                title = "Masking",
                subtitle = "Guide attention with linear, radial and brush selections.",
                onClick = onOpenMasking,
            )
            ToolLinkCard(
                glyph = PhotoCraftGlyph.Portrait,
                title = "Portrait",
                subtitle = "Sculpt light, skin and background depth.",
                onClick = onOpenPortrait,
            )

            Text(
                text = "Rotation ${edit.rotationQuarterTurns * 90}°  ·  " +
                    if (edit.flipHorizontal) {
                        "Flipped horizontally"
                    } else {
                        "Original orientation"
                    },
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun ToolLinkCard(
    glyph: PhotoCraftGlyph,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    PhotoCraftCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(PhotoCraftSpacing.md),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(PhotoCraftCornerRadius.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhotoCraftIcon(
                glyph = glyph,
                tint = colors.gold,
            )
            Spacer(modifier = Modifier.width(PhotoCraftSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            PhotoCraftIcon(
                glyph = PhotoCraftGlyph.Back,
                modifier = Modifier.rotate(180f),
                tint = colors.textSecondary,
            )
        }
    }
}
