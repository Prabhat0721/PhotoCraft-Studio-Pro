package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PhotoCraftSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    valueLabel: String? = null,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.photoCraftColors
    val hasHeader = !label.isNullOrBlank() || !valueLabel.isNullOrBlank()
    val sliderModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = PhotoCraftDimensions.minTouchTarget)
        .then(
            if (!label.isNullOrBlank()) {
                Modifier.semantics { contentDescription = label }
            } else {
                Modifier
            },
        )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xs),
    ) {
        if (hasHeader) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!label.isNullOrBlank()) {
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) colors.textPrimary else colors.textSecondary,
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (!valueLabel.isNullOrBlank()) {
                    Text(
                        text = valueLabel,
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                        color = if (enabled) colors.gold else colors.textSecondary,
                    )
                }
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = sliderModifier,
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors(
                thumbColor = colors.gold,
                activeTrackColor = colors.gold,
                activeTickColor = PhotoCraftOnGold,
                inactiveTrackColor = colors.elevated,
                inactiveTickColor = colors.textSecondary,
                disabledThumbColor = colors.divider,
                disabledActiveTrackColor = colors.divider,
                disabledActiveTickColor = colors.surface,
                disabledInactiveTrackColor = colors.card,
                disabledInactiveTickColor = colors.divider,
            ),
        )
    }
}
