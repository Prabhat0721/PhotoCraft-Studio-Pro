package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCardShape
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PhotoCraftCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.photoCraftColors.card,
    contentColor: Color = MaterialTheme.photoCraftColors.textPrimary,
    shape: Shape = PhotoCraftCardShape,
    contentPadding: PaddingValues = PaddingValues(PhotoCraftSpacing.lg),
    bordered: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val border = if (bordered) {
        BorderStroke(
            width = PhotoCraftDimensions.dividerThickness,
            color = MaterialTheme.photoCraftColors.divider,
        )
    } else {
        null
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
        ) {
            PhotoCraftCardBody(contentPadding = contentPadding, content = content)
        }
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
        ) {
            PhotoCraftCardBody(contentPadding = contentPadding, content = content)
        }
    }
}

@Composable
private fun PhotoCraftCardBody(
    contentPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.padding(contentPadding),
        content = content,
    )
}
