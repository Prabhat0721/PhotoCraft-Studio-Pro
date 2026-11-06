package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftColorChip
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftValueLabel
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
internal fun HslPanel(
    hsl: HslAdjustments,
    selectedChannel: HslChannel,
    onChannelSelected: (HslChannel) -> Unit,
    onValueChange: (HslChannel, HslComponent, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors
    val adjustment = hsl.valueFor(selectedChannel)

    EditorPanelContainer(
        title = "HSL",
        subtitle = "Select a color family, then shape its hue, saturation and luminance.",
        modifier = modifier,
        action = {
            PhotoCraftValueLabel(
                value = selectedChannel.label,
                valueColor = colors.gold,
            )
        },
    ) {
        HslChannelSelector(
            selectedChannel = selectedChannel,
            onChannelSelected = onChannelSelected,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PhotoCraftDimensions.screenPadding,
                    end = PhotoCraftDimensions.screenPadding,
                    top = PhotoCraftSpacing.sm,
                ),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xxs),
        ) {
            HslComponent.entries.forEach { component ->
                EditorAdjustmentSlider(
                    label = component.label,
                    value = adjustment.valueFor(component),
                    onValueChange = { value ->
                        onValueChange(selectedChannel, component, value)
                    },
                    onValueChangeFinished = onValueChangeFinished,
                )
            }
        }
    }
}

@Composable
private fun HslChannelSelector(
    selectedChannel: HslChannel,
    onChannelSelected: (HslChannel) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = PhotoCraftDimensions.screenPadding,
            vertical = PhotoCraftSpacing.xs,
        ),
        horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xs),
    ) {
        items(items = HslChannel.entries, key = { it.name }) { channel ->
            val selected = channel == selectedChannel
            Column(
                modifier = Modifier.width(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PhotoCraftColorChip(
                    color = Color.hsv(channel.hue, 0.78f, 0.95f),
                    label = channel.label,
                    selected = selected,
                    onClick = { onChannelSelected(channel) },
                )
                Text(
                    text = channel.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) colors.gold else colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
