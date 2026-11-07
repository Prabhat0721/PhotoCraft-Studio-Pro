package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PhotoCraftIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean? = null,
    contentDescription: String? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    bordered: Boolean = true,
    borderColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    val isSelected = selected == true
    val resolvedContainerColor = when {
        containerColor != null -> containerColor
        !enabled -> colors.card
        isSelected -> colors.gold
        else -> colors.elevated
    }
    val resolvedContentColor = when {
        contentColor != null -> contentColor
        !enabled -> colors.textSecondary
        isSelected -> colors.gold
        else -> colors.textPrimary
    }
    val resolvedBorderColor = when {
        borderColor != null -> borderColor
        !enabled -> colors.divider
        isSelected -> colors.gold
        else -> colors.divider
    }
    val semanticsModifier = if (contentDescription != null || selected != null) {
        Modifier.semantics {
            if (contentDescription != null) {
                this.contentDescription = contentDescription
            }
            if (selected != null) {
                this.selected = selected
            }
        }
    } else {
        Modifier
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .size(PhotoCraftDimensions.minTouchTarget)
            .then(semanticsModifier),
        enabled = enabled,
        shape = CircleShape,
        color = resolvedContainerColor,
        contentColor = resolvedContentColor,
        border = if (bordered) {
            BorderStroke(PhotoCraftDimensions.dividerThickness, resolvedBorderColor)
        } else {
            null
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(PhotoCraftSpacing.md),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
