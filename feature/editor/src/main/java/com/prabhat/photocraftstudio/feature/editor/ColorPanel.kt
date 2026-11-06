package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing

@Composable
internal fun ColorPanel(
    color: ColorAdjustments,
    onValueChange: (ColorAdjustment, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditorPanelContainer(
        title = "Color",
        subtitle = "Refine white balance, saturation and vibrance.",
        modifier = modifier,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth >= 600.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PhotoCraftDimensions.screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.lg),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xxs),
                    ) {
                        ColorAdjustment.entries.take(2).forEach { adjustment ->
                            ColorControl(
                                adjustment = adjustment,
                                color = color,
                                onValueChange = onValueChange,
                                onValueChangeFinished = onValueChangeFinished,
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xxs),
                    ) {
                        ColorAdjustment.entries.drop(2).forEach { adjustment ->
                            ColorControl(
                                adjustment = adjustment,
                                color = color,
                                onValueChange = onValueChange,
                                onValueChangeFinished = onValueChangeFinished,
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PhotoCraftDimensions.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xxs),
                ) {
                    ColorAdjustment.entries.forEach { adjustment ->
                        ColorControl(
                            adjustment = adjustment,
                            color = color,
                            onValueChange = onValueChange,
                            onValueChangeFinished = onValueChangeFinished,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorControl(
    adjustment: ColorAdjustment,
    color: ColorAdjustments,
    onValueChange: (ColorAdjustment, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    EditorAdjustmentSlider(
        label = adjustment.label,
        value = color.valueFor(adjustment),
        onValueChange = { onValueChange(adjustment, it) },
        onValueChangeFinished = onValueChangeFinished,
    )
}
