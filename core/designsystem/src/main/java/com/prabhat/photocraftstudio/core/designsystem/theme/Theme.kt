package com.prabhat.photocraftstudio.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPhotoCraftColors = staticCompositionLocalOf { DarkPhotoCraftColors }

private val PhotoCraftDarkColorScheme = darkColorScheme(
    primary = PhotoCraftGold,
    onPrimary = PhotoCraftOnGold,
    primaryContainer = PhotoCraftGoldContainer,
    onPrimaryContainer = PhotoCraftOnGoldContainer,
    secondary = PhotoCraftTextSecondary,
    onSecondary = PhotoCraftBackground,
    secondaryContainer = PhotoCraftElevated,
    onSecondaryContainer = PhotoCraftTextPrimary,
    tertiary = PhotoCraftChampagne,
    onTertiary = PhotoCraftOnGold,
    tertiaryContainer = PhotoCraftGoldContainer,
    onTertiaryContainer = PhotoCraftOnGoldContainer,
    error = PhotoCraftError,
    onError = PhotoCraftOnError,
    errorContainer = PhotoCraftErrorContainer,
    onErrorContainer = PhotoCraftOnErrorContainer,
    background = PhotoCraftBackground,
    onBackground = PhotoCraftTextPrimary,
    surface = PhotoCraftSurface,
    onSurface = PhotoCraftTextPrimary,
    surfaceVariant = PhotoCraftCard,
    onSurfaceVariant = PhotoCraftTextSecondary,
    surfaceTint = PhotoCraftGold,
    inverseSurface = PhotoCraftTextPrimary,
    inverseOnSurface = PhotoCraftBackground,
    inversePrimary = PhotoCraftGoldDeep,
    outline = PhotoCraftDivider,
    outlineVariant = PhotoCraftDivider,
    scrim = PhotoCraftScrim,
    surfaceBright = PhotoCraftElevated,
    surfaceDim = PhotoCraftBackground,
    surfaceContainerLowest = PhotoCraftBackground,
    surfaceContainerLow = PhotoCraftSurface,
    surfaceContainer = PhotoCraftCard,
    surfaceContainerHigh = PhotoCraftElevated,
    surfaceContainerHighest = PhotoCraftElevatedHigh,
)

@Composable
fun PhotoCraftTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPhotoCraftColors provides DarkPhotoCraftColors) {
        MaterialTheme(
            colorScheme = PhotoCraftDarkColorScheme,
            typography = PhotoCraftTypography,
            shapes = PhotoCraftShapes,
            content = content,
        )
    }
}

val MaterialTheme.photoCraftColors: PhotoCraftColors
    @Composable
    @ReadOnlyComposable
    get() = LocalPhotoCraftColors.current
