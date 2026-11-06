package com.prabhat.photocraftstudio.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object PhotoCraftCornerRadius {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val card = PhotoCraftDimensions.cardRadius
    val panel = PhotoCraftDimensions.panelRadius
    val lg = 28.dp
    val pill = 999.dp
}

val PhotoCraftCardShape = RoundedCornerShape(PhotoCraftCornerRadius.card)
val PhotoCraftPanelShape = RoundedCornerShape(PhotoCraftCornerRadius.panel)

val PhotoCraftShapes = Shapes(
    extraSmall = RoundedCornerShape(PhotoCraftCornerRadius.xs),
    small = RoundedCornerShape(PhotoCraftCornerRadius.sm),
    medium = RoundedCornerShape(PhotoCraftCornerRadius.card),
    large = RoundedCornerShape(PhotoCraftCornerRadius.panel),
    extraLarge = RoundedCornerShape(PhotoCraftCornerRadius.lg),
)
