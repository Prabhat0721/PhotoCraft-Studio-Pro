package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PhotoCraftValueLabel(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    valueColor: Color = MaterialTheme.photoCraftColors.gold,
    containerColor: Color = MaterialTheme.photoCraftColors.elevated,
) {
    val colors = MaterialTheme.photoCraftColors

    Surface(
        modifier = modifier.semantics(mergeDescendants = true) {},
        shape = RoundedCornerShape(PhotoCraftCornerRadius.pill),
        color = containerColor,
        contentColor = valueColor,
        border = BorderStroke(PhotoCraftDimensions.dividerThickness, colors.divider),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = PhotoCraftSpacing.md,
                vertical = PhotoCraftSpacing.xs,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!label.isNullOrBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                Spacer(modifier = Modifier.width(PhotoCraftSpacing.sm))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                color = valueColor,
            )
        }
    }
}
