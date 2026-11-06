package com.prabhat.photocraftstudio.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val PhotoCraftBackground = Color(0xFF101114)
val PhotoCraftSurface = Color(0xFF17181C)
val PhotoCraftCard = Color(0xFF1D1E23)
val PhotoCraftElevated = Color(0xFF25262C)
val PhotoCraftGold = Color(0xFFC8A96E)
val PhotoCraftTextPrimary = Color(0xFFF5F5F5)
val PhotoCraftTextSecondary = Color(0xFF999BA3)
val PhotoCraftDivider = Color(0xFF303137)

val PhotoCraftGoldDeep = Color(0xFF6E5528)
val PhotoCraftChampagne = Color(0xFFE7D6B3)
val PhotoCraftGoldContainer = Color(0xFF3C3220)
val PhotoCraftOnGoldContainer = Color(0xFFF0E2C6)
val PhotoCraftOnGold = Color(0xFF1A1408)
val PhotoCraftElevatedHigh = Color(0xFF2E2F36)
val PhotoCraftScrim = Color(0xCC000000)
val PhotoCraftError = Color(0xFFF2B8B5)
val PhotoCraftOnError = Color(0xFF601410)
val PhotoCraftErrorContainer = Color(0xFF8C1D18)
val PhotoCraftOnErrorContainer = Color(0xFFF9DEDC)

@Immutable
data class PhotoCraftColors(
    val background: Color,
    val surface: Color,
    val card: Color,
    val elevated: Color,
    val gold: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
)

val DarkPhotoCraftColors = PhotoCraftColors(
    background = PhotoCraftBackground,
    surface = PhotoCraftSurface,
    card = PhotoCraftCard,
    elevated = PhotoCraftElevated,
    gold = PhotoCraftGold,
    textPrimary = PhotoCraftTextPrimary,
    textSecondary = PhotoCraftTextSecondary,
    divider = PhotoCraftDivider,
)
