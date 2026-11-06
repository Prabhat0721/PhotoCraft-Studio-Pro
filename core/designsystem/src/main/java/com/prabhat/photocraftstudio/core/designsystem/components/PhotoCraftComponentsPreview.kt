package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftTheme
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101114,
    widthDp = 400,
    heightDp = 760,
)
@Composable
private fun PhotoCraftComponentsPreview() {
    PhotoCraftTheme {
        var selectedCategory by remember { mutableStateOf("Adjust") }
        var exposure by remember { mutableFloatStateOf(0.4f) }
        var selectedColor by remember { mutableIntStateOf(0) }
        val swatches = listOf(
            Color(0xFFC8A96E),
            Color(0xFFF5F5F5),
            Color(0xFF7EA6C8),
            Color(0xFFB57E7E),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.photoCraftColors.background)
                .padding(PhotoCraftSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xl),
        ) {
            PhotoCraftSectionHeader(
                title = "Adjustments",
                subtitle = "Fine-tune your photo",
            )

            PhotoCraftCategoryTabs(
                categories = listOf("Adjust", "Filters", "Crop", "Effects"),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                contentPadding = PaddingValues(0.dp),
            )

            PhotoCraftCard {
                Text(
                    text = "Premium card",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.photoCraftColors.textPrimary,
                )
                Text(
                    text = "A reusable dark surface with a champagne-gold accent.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.photoCraftColors.textSecondary,
                )
            }

            PhotoCraftValueLabel(label = "Temperature", value = "5200 K")

            PhotoCraftSlider(
                value = exposure,
                onValueChange = { exposure = it },
                label = "Exposure",
                valueLabel = "%+.1f EV".format(exposure),
                valueRange = -1f..1f,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm)) {
                swatches.forEachIndexed { index, color ->
                    PhotoCraftColorChip(
                        color = color,
                        label = "Color ${index + 1}",
                        selected = selectedColor == index,
                        onClick = { selectedColor = index },
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md)) {
                PhotoCraftButton(onClick = {}) {
                    Text("Apply")
                }
                PhotoCraftButton(
                    onClick = {},
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text("Reset")
                }
                PhotoCraftButton(
                    onClick = {},
                    variant = PhotoCraftButtonVariant.Text,
                ) {
                    Text("Cancel")
                }
            }

            PhotoCraftDivider()
        }
    }
}
