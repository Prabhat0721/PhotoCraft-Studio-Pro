package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun PhotoCraftCategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = PhotoCraftDimensions.screenPadding),
) {
    val colors = MaterialTheme.photoCraftColors
    val shape = RoundedCornerShape(PhotoCraftCornerRadius.pill)

    LazyRow(
        modifier = modifier.selectableGroup(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
    ) {
        items(items = categories) { category ->
            val selected = category == selectedCategory
            Surface(
                modifier = Modifier
                    .heightIn(min = PhotoCraftDimensions.minTouchTarget)
                    .clip(shape)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onCategorySelected(category) },
                    ),
                shape = shape,
                color = if (selected) colors.gold else colors.elevated,
                contentColor = if (selected) PhotoCraftOnGold else colors.textSecondary,
                border = BorderStroke(
                    width = PhotoCraftDimensions.dividerThickness,
                    color = if (selected) colors.gold else colors.divider,
                ),
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = PhotoCraftSpacing.xl,
                        vertical = PhotoCraftSpacing.md,
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
