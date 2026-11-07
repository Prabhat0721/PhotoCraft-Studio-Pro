package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftColorWheel
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftSlider
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors
import kotlin.math.roundToInt

@Composable
internal fun ColorGradingPanel(
    grading: ColorGradingAdjustments,
    onWheelChange: (GradingScope, Float, Float) -> Unit,
    onLuminanceChange: (GradingScope, Float) -> Unit,
    onBlendChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditorPanelContainer(
        title = "Color Grading",
        subtitle = "Blend cinematic color into shadows, midtones, highlights and globally.",
        modifier = modifier,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth >= 600.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = PhotoCraftDimensions.screenPadding,
                            end = PhotoCraftDimensions.screenPadding,
                            top = PhotoCraftSpacing.xs,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
                ) {
                    GradingScope.entries.forEach { scope ->
                        GradingControl(
                            scope = scope,
                            value = grading.valueFor(scope),
                            onWheelChange = { hue, saturation ->
                                onWheelChange(scope, hue, saturation)
                            },
                            onLuminanceChange = { value ->
                                onLuminanceChange(scope, value)
                            },
                            onValueChangeFinished = onValueChangeFinished,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = PhotoCraftDimensions.screenPadding,
                        vertical = PhotoCraftSpacing.xs,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
                ) {
                    items(items = GradingScope.entries, key = { it.name }) { scope ->
                        GradingControl(
                            scope = scope,
                            value = grading.valueFor(scope),
                            onWheelChange = { hue, saturation ->
                                onWheelChange(scope, hue, saturation)
                            },
                            onLuminanceChange = { value ->
                                onLuminanceChange(scope, value)
                            },
                            onValueChangeFinished = onValueChangeFinished,
                            modifier = Modifier.width(152.dp),
                        )
                    }
                }
            }
        }
    }

    PhotoCraftSlider(
        value = grading.blend,
        onValueChange = onBlendChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PhotoCraftDimensions.screenPadding),
        label = "Blend",
        valueLabel = "${(grading.blend * 100f).roundToInt()}%",
        valueRange = 0f..1f,
        onValueChangeFinished = onValueChangeFinished,
    )
}

@Composable
private fun GradingControl(
    scope: GradingScope,
    value: ColorWheelValue,
    onWheelChange: (Float, Float) -> Unit,
    onLuminanceChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = scope.label,
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
        PhotoCraftColorWheel(
            hue = value.hue,
            saturation = value.saturation,
            onHueSaturationChange = onWheelChange,
            onValueChangeFinished = onValueChangeFinished,
            modifier = Modifier.size(132.dp),
        )
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "H ${value.hue.roundToInt()}°",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = colors.textSecondary,
            )
            Text(
                text = "S ${(value.saturation * 100f).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Luminance",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatSignedPercent(value.luminance),
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = colors.gold,
            )
        }
        PhotoCraftSlider(
            value = value.luminance,
            onValueChange = onLuminanceChange,
            modifier = Modifier.fillMaxWidth(),
            valueRange = -1f..1f,
            onValueChangeFinished = onValueChangeFinished,
        )
    }
}
