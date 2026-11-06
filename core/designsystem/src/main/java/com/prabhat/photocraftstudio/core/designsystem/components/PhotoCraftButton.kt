package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

enum class PhotoCraftButtonVariant {
    Primary,
    Secondary,
    Text,
}

@Composable
fun PhotoCraftButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: PhotoCraftButtonVariant = PhotoCraftButtonVariant.Primary,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    val buttonModifier = modifier.heightIn(min = PhotoCraftDimensions.minTouchTarget)
    val shape = RoundedCornerShape(PhotoCraftCornerRadius.md)
    val contentPadding = PaddingValues(
        horizontal = PhotoCraftSpacing.xl,
        vertical = PhotoCraftSpacing.md,
    )

    when (variant) {
        PhotoCraftButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.gold,
                contentColor = PhotoCraftOnGold,
                disabledContainerColor = colors.elevated,
                disabledContentColor = colors.textSecondary,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp,
            ),
            contentPadding = contentPadding,
            content = content,
        )

        PhotoCraftButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.gold,
                disabledContentColor = colors.textSecondary,
            ),
            border = BorderStroke(
                width = PhotoCraftDimensions.dividerThickness,
                color = if (enabled) colors.gold else colors.divider,
            ),
            contentPadding = contentPadding,
            content = content,
        )

        PhotoCraftButtonVariant.Text -> TextButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = colors.gold,
                disabledContentColor = colors.textSecondary,
            ),
            contentPadding = contentPadding,
            content = content,
        )
    }
}
