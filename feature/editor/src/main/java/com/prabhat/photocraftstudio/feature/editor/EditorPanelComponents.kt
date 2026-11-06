package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftDivider
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftSlider
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors
import kotlin.math.roundToInt

@Composable
internal fun EditorPanelContainer(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp)
            .then(
                if (scrollable) {
                    Modifier.verticalScroll(rememberScrollState())
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PhotoCraftDimensions.screenPadding,
                    end = PhotoCraftDimensions.screenPadding,
                    top = PhotoCraftSpacing.sm,
                    bottom = PhotoCraftSpacing.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(PhotoCraftSpacing.xxs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (action != null) {
                Spacer(modifier = Modifier.width(PhotoCraftSpacing.md))
                action()
            }
        }
        PhotoCraftDivider(
            modifier = Modifier.padding(horizontal = PhotoCraftDimensions.screenPadding),
        )
        content()
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.sm))
    }
}

@Composable
internal fun EditorAdjustmentSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PhotoCraftSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        valueLabel = formatSignedPercent(value),
        valueRange = -1f..1f,
        onValueChangeFinished = onValueChangeFinished,
    )
}

internal fun formatSignedPercent(value: Float): String {
    val percent = (value * 100f).roundToInt()
    return if (percent > 0) "+$percent" else percent.toString()
}
