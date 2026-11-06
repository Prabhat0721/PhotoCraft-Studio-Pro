package com.prabhat.photocraftstudio.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prabhat.photocraftstudio.BuildConfig
import com.prabhat.photocraftstudio.core.data.AppSettingsRepository
import com.prabhat.photocraftstudio.core.data.GalleryDensity
import com.prabhat.photocraftstudio.core.data.StudioSettings
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButtonVariant
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCard
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCategoryTabs
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftDivider
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftValueLabel
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun SettingsRoute(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val repository = remember(context) { AppSettingsRepository.get(context) }
    val settings by repository.settings.collectAsState()

    SettingsScreen(
        settings = settings,
        onGalleryDensityChange = repository::setGalleryDensity,
        onShowPhotoLabelsChange = repository::setShowPhotoLabels,
        onKeepScreenOnChange = repository::setKeepScreenOnInEditor,
        onReset = repository::reset,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    settings: StudioSettings,
    onGalleryDensityChange: (GalleryDensity) -> Unit,
    onShowPhotoLabelsChange: (Boolean) -> Unit,
    onKeepScreenOnChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.surface, colors.background),
                ),
            ),
    ) {
        SettingsHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = PhotoCraftDimensions.screenPadding,
                    end = PhotoCraftDimensions.screenPadding,
                    bottom = PhotoCraftSpacing.xxl,
                ),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.md),
        ) {
            SettingsSection(
                title = "Gallery",
                subtitle = "Choose how the library is presented.",
            ) {
                Text(
                    text = "Grid density",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
                PhotoCraftCategoryTabs(
                    categories = GalleryDensity.entries.map { it.label },
                    selectedCategory = settings.galleryDensity.label,
                    onCategorySelected = { label ->
                        GalleryDensity.entries
                            .firstOrNull { it.label == label }
                            ?.let(onGalleryDensityChange)
                    },
                    contentPadding = PaddingValues(0.dp),
                )
                Spacer(modifier = Modifier.height(PhotoCraftSpacing.md))
                PhotoCraftDivider()
                Spacer(modifier = Modifier.height(PhotoCraftSpacing.md))
                SettingsToggleRow(
                    label = "Photo labels",
                    description = "Show names and albums over gallery thumbnails.",
                    checked = settings.showPhotoLabels,
                    onCheckedChange = onShowPhotoLabelsChange,
                )
            }

            SettingsSection(
                title = "Editing",
                subtitle = "Behavior that follows you into the workspace.",
            ) {
                SettingsToggleRow(
                    label = "Keep screen on",
                    description = "Prevent the display from sleeping while editing.",
                    checked = settings.keepScreenOnInEditor,
                    onCheckedChange = onKeepScreenOnChange,
                )
            }

            PhotoCraftCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PhotoCraftIcon(
                        glyph = PhotoCraftGlyph.Settings,
                        modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
                        tint = colors.gold,
                    )
                    Spacer(modifier = Modifier.width(PhotoCraftSpacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PhotoCraft Studio",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "Luxury dark editing workspace",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                    PhotoCraftValueLabel(value = "v${BuildConfig.VERSION_NAME}")
                }
            }

            PhotoCraftCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset settings",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "Restore the original gallery and editing defaults.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                        )
                    }
                    Spacer(modifier = Modifier.width(PhotoCraftSpacing.md))
                    PhotoCraftButton(
                        onClick = onReset,
                        variant = PhotoCraftButtonVariant.Secondary,
                    ) {
                        Text(text = "Reset")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    val colors = MaterialTheme.photoCraftColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = PhotoCraftDimensions.screenPadding,
                end = PhotoCraftDimensions.screenPadding,
                top = PhotoCraftSpacing.md,
                bottom = PhotoCraftSpacing.lg,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "STUDIO PREFERENCES",
                style = MaterialTheme.typography.labelSmall,
                color = colors.gold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
            )
            Spacer(modifier = Modifier.height(PhotoCraftSpacing.xxs))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
            )
            Text(
                text = "Tune the studio to your workflow",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        PhotoCraftValueLabel(value = "Saved")
    }
}

@Composable
private fun SettingsSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.sm))
        PhotoCraftCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(PhotoCraftSpacing.lg),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(PhotoCraftSpacing.md))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PhotoCraftOnGold,
                checkedTrackColor = colors.gold,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.elevated,
                uncheckedBorderColor = colors.divider,
            ),
        )
    }
}
