package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

enum class PhotoCraftDividerOrientation {
    Horizontal,
    Vertical,
}

@Composable
fun PhotoCraftDivider(
    modifier: Modifier = Modifier,
    orientation: PhotoCraftDividerOrientation = PhotoCraftDividerOrientation.Horizontal,
    color: Color = MaterialTheme.photoCraftColors.divider,
    thickness: Dp = PhotoCraftDimensions.dividerThickness,
) {
    when (orientation) {
        PhotoCraftDividerOrientation.Horizontal -> HorizontalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color,
        )

        PhotoCraftDividerOrientation.Vertical -> VerticalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color,
        )
    }
}
