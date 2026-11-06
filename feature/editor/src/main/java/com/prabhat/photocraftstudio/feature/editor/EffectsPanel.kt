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
internal fun EffectsPanel(
    effects: EffectAdjustments,
    onValueChange: (EffectAdjustment, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditorPanelContainer(
        title = "Effects",
        subtitle = "Shape texture, atmosphere and the edges of the frame.",
        modifier = modifier,
    ) {
        AdjustmentGrid(
            entries = EffectAdjustment.entries,
            valueFor = effects::valueFor,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            labelFor = { it.label },
        )
    }
}

@Composable
internal fun DetailPanel(
    detail: DetailAdjustments,
    onValueChange: (DetailAdjustment, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditorPanelContainer(
        title = "Detail",
        subtitle = "Refine micro-contrast and clean up the final image.",
        modifier = modifier,
    ) {
        AdjustmentGrid(
            entries = DetailAdjustment.entries,
            valueFor = detail::valueFor,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            labelFor = { it.label },
        )
    }
}

@Composable
private fun <T> AdjustmentGrid(
    entries: List<T>,
    valueFor: (T) -> Float,
    onValueChange: (T, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    labelFor: (T) -> String = { it.toString() },
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth >= 600.dp) {
            val split = (entries.size + 1) / 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PhotoCraftDimensions.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.lg),
            ) {
                AdjustmentColumn(
                    entries = entries.take(split),
                    valueFor = valueFor,
                    onValueChange = onValueChange,
                    onValueChangeFinished = onValueChangeFinished,
                    labelFor = labelFor,
                    modifier = Modifier.weight(1f),
                )
                AdjustmentColumn(
                    entries = entries.drop(split),
                    valueFor = valueFor,
                    onValueChange = onValueChange,
                    onValueChangeFinished = onValueChangeFinished,
                    labelFor = labelFor,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            AdjustmentColumn(
                entries = entries,
                valueFor = valueFor,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                labelFor = labelFor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PhotoCraftDimensions.screenPadding),
            )
        }
    }
}

@Composable
private fun <T> AdjustmentColumn(
    entries: List<T>,
    valueFor: (T) -> Float,
    onValueChange: (T, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    labelFor: (T) -> String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xxs),
    ) {
        entries.forEach { entry ->
            EditorAdjustmentSlider(
                label = labelFor(entry),
                value = valueFor(entry),
                onValueChange = { onValueChange(entry, it) },
                onValueChangeFinished = onValueChangeFinished,
            )
        }
    }
}
