package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCategoryTabs
import com.prabhat.photocraftstudio.core.designsystem.components.loadPhotoBitmap
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIconButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftValueLabel
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftOnGold
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

private val editorCategories = listOf(
    "Light",
    "Color",
    "HSL",
    "Grading",
    "Curves",
    "Effects",
    "Detail",
    "Filters",
    "Tools",
)

@Composable
fun EditorScreen(
    uiState: EditorUiState,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    onExport: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onReset: () -> Unit,
    onFilterSelected: (PhotoFilter) -> Unit,
    onLightChange: (LightAdjustment, Float) -> Unit,
    onColorChange: (ColorAdjustment, Float) -> Unit,
    onHslChange: (HslChannel, HslComponent, Float) -> Unit,
    onGradingWheelChange: (GradingScope, Float, Float) -> Unit,
    onGradingLuminanceChange: (GradingScope, Float) -> Unit,
    onBlendChange: (Float) -> Unit,
    onCurveChange: (RgbChannel, Float, Float) -> Unit,
    onEffectChange: (EffectAdjustment, Float) -> Unit,
    onDetailChange: (DetailAdjustment, Float) -> Unit,
    onGestureFinished: () -> Unit,
    onRotate: () -> Unit,
    onFlip: () -> Unit,
    onOpenCrop: () -> Unit,
    onOpenMasking: () -> Unit,
    onOpenPortrait: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors
    val photo = uiState.photo
    val edit = uiState.edit
    val context = LocalContext.current
    val previewBitmap by produceState<Bitmap?>(initialValue = null, photo?.imageModel) {
        value = withContext(Dispatchers.IO) { loadPhotoBitmap(context, photo?.imageModel, 2048) }
    }
    var selectedCategory by rememberSaveable { mutableStateOf(editorCategories.first()) }
    var selectedHslChannelIndex by rememberSaveable { mutableStateOf(0) }
    var comparing by rememberSaveable(photo?.id) { mutableStateOf(false) }
    var comparisonValue by remember { mutableStateOf(0.5f) }
    val comparisonViewportSize = remember { mutableStateOf(IntSize(0, 0)) }
    val activeCategory = if (selectedCategory in editorCategories) {
        selectedCategory
    } else {
        editorCategories.first()
    }
    val selectedHslChannel = HslChannel.entries.getOrElse(selectedHslChannelIndex) { HslChannel.Red }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        EditorHeader(
            photoName = photo?.name,
            isEdited = edit.isEdited,
            comparing = comparing,
            hasPhoto = photo != null,
            onBack = onBack,
            onCompareChange = { comparing = it },
            onExport = onExport,
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = PhotoCraftDimensions.screenPadding, vertical = PhotoCraftSpacing.sm)
                .clip(RoundedCornerShape(PhotoCraftCornerRadius.panel))
                .background(Color.Black),
        ) {
            if (photo == null) {
                EmptyEditor(onOpenGallery = onOpenGallery)
            } else {
                val rotationScale = if (edit.rotationQuarterTurns % 2 == 0) {
                    1f
                } else {
                    minOf(maxWidth / maxHeight, maxHeight / maxWidth)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = edit.rotationQuarterTurns * 90f
                            scaleX = rotationScale * if (edit.flipHorizontal) -1f else 1f
                            scaleY = rotationScale
                        },
                ) {
                    val adjustedBitmap = remember(photo, edit) {
                        previewBitmap?.let { bitmap ->
                            var target = Bitmap.createBitmap(
                                bitmap,
                                0,
                               0,
                                bitmap.width,
                                bitmap.height,
                                Matrix().apply { setValues(edit.toColorMatrixValues()) },
                                true,
                            )
                            for (mask in edit.masks) {
                                if (!mask.enabled) continue
                                val masked = PhotoExporter.applyLocalAdjustments(target, mask, mask.adjustments)
                                if (masked !== target) {
                                    target.recycle()
                                    target = masked
                                }
                            }
                            val portraitResult = PhotoExporter.applyPortrait(target, edit.portrait)
                            if (portraitResult !== target) {
                                target.recycle()
                                target = portraitResult
                            }
                            PhotoExporter.applyVignette(target, edit.effects.vignette)
                            PhotoExporter.applyGrain(target, edit.effects.grain)
                            PhotoExporter.applyRgbCurves(target, edit.curves)
                            target
                        }
                    }
                    val adjustedImage = adjustedBitmap?.asImageBitmap()

                    if (comparing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { coordinates -> comparisonViewportSize.value = coordinates.size }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, _ ->
                                        val viewport = comparisonViewportSize.value
                                        val width = viewport.width.coerceAtLeast(1)
                                        val fraction = (change.position.x / width.toFloat()).coerceIn(0f, 1f)
                                        comparisonValue = fraction
                                    }
                                },
                        ) {
                            previewBitmap?.asImageBitmap()?.let { originalImage ->
                                Image(
                                    bitmap = originalImage,
                                    contentDescription = photo.name,
                                    modifier = Modifier.fillMaxWidth(comparisonValue).fillMaxHeight(),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                            adjustedImage?.let { editedImage ->
                                Image(
                                    bitmap = editedImage,
                                    contentDescription = photo.name,
                                    modifier = Modifier.fillMaxWidth((1f - comparisonValue).coerceAtLeast(0.01f)).fillMaxHeight(),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }
                    } else if (adjustedImage != null) {
                        Image(
                            bitmap = adjustedImage,
                            contentDescription = photo.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        edit.masks.filter { it.enabled }.forEach { mask ->
                            drawMask(mask)
                        }
                    }
                }

                PhotoCraftValueLabel(
                    value = when {
                        comparing -> "Original"
                        edit.filter != PhotoFilter.Original -> edit.filter.label
                        edit.isEdited -> "Edited"
                        else -> "Original"
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(PhotoCraftSpacing.sm),
                    containerColor = Color.Black.copy(alpha = 0.55f),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PhotoCraftDimensions.screenPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
        ) {
            ToolbarIconButton(
                glyph = PhotoCraftGlyph.Undo,
                contentDescription = "Undo",
                enabled = uiState.canUndo,
                onClick = onUndo,
            )
            ToolbarIconButton(
                glyph = PhotoCraftGlyph.Redo,
                contentDescription = "Redo",
                enabled = uiState.canRedo,
                onClick = onRedo,
            )
            ToolbarIconButton(
                glyph = PhotoCraftGlyph.Reset,
                contentDescription = "Reset edits",
                enabled = edit.isEdited,
                onClick = onReset,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (edit.isEdited) {
                PhotoCraftValueLabel(value = "Edited", valueColor = colors.gold)
            }
        }

        PhotoCraftCategoryTabs(
            categories = editorCategories,
            selectedCategory = activeCategory,
            onCategorySelected = { selectedCategory = it },
            modifier = Modifier.padding(top = PhotoCraftSpacing.xs),
            contentPadding = PaddingValues(horizontal = PhotoCraftDimensions.screenPadding),
        )

        when (activeCategory) {
            "Light" -> LightPanel(
                light = edit.light,
                onValueChange = onLightChange,
                onValueChangeFinished = onGestureFinished,
            )

            "Color" -> ColorPanel(
                color = edit.color,
                onValueChange = onColorChange,
                onValueChangeFinished = onGestureFinished,
            )

            "HSL" -> HslPanel(
                hsl = edit.hsl,
                selectedChannel = selectedHslChannel,
                onChannelSelected = { selectedHslChannelIndex = it.ordinal },
                onValueChange = onHslChange,
                onValueChangeFinished = onGestureFinished,
            )

            "Grading" -> ColorGradingPanel(
                grading = edit.grading,
                onWheelChange = onGradingWheelChange,
                onLuminanceChange = onGradingLuminanceChange,
                onBlendChange = onBlendChange,
                onValueChangeFinished = onGestureFinished,
            )

            "Curves" -> CurvesPanel(
                curves = edit.curves,
                onCurveChange = onCurveChange,
                onValueChangeFinished = onGestureFinished,
            )

            "Effects" -> EffectsPanel(
                effects = edit.effects,
                onValueChange = onEffectChange,
                onValueChangeFinished = onGestureFinished,
            )

            "Detail" -> DetailPanel(
                detail = edit.detail,
                onValueChange = onDetailChange,
                onValueChangeFinished = onGestureFinished,
            )

            "Filters" -> FilterPanel(
                selectedFilter = edit.filter,
                onFilterSelected = onFilterSelected,
            )

            else -> ToolsPanel(
                edit = edit,
                onRotate = onRotate,
                onFlip = onFlip,
                onReset = onReset,
                onOpenCrop = onOpenCrop,
                onOpenMasking = onOpenMasking,
                onOpenPortrait = onOpenPortrait,
            )
        }
    }
}

@Composable
private fun EditorHeader(
    photoName: String?,
    isEdited: Boolean,
    comparing: Boolean,
    hasPhoto: Boolean,
    onBack: () -> Unit,
    onCompareChange: (Boolean) -> Unit,
    onExport: () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    val status = when {
        photoName == null -> "Select a photo to begin"
        comparing -> "Showing original"
        isEdited -> "Edited"
        else -> "No edits yet"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PhotoCraftSpacing.sm, vertical = PhotoCraftSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PhotoCraftIconButton(
            onClick = onBack,
            contentDescription = "Back",
            containerColor = Color.Transparent,
            bordered = false,
        ) {
            PhotoCraftIcon(glyph = PhotoCraftGlyph.Back)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = PhotoCraftSpacing.sm),
        ) {
            Text(
                text = photoName ?: "Editing workspace",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = if (isEdited || comparing) colors.gold else colors.textSecondary,
            )
        }
        PhotoCraftIconButton(
            onClick = { onCompareChange(!comparing) },
            enabled = hasPhoto,
            selected = if (comparing) true else null,
            contentDescription = if (comparing) "Show edited photo" else "Compare with original",
        ) {
            PhotoCraftIcon(glyph = PhotoCraftGlyph.Compare)
        }
        Spacer(modifier = Modifier.width(PhotoCraftSpacing.xs))
        PhotoCraftIconButton(
            onClick = onExport,
            enabled = hasPhoto,
            contentDescription = "Export edited photo",
            containerColor = if (hasPhoto) colors.gold else colors.elevated,
            contentColor = if (hasPhoto) PhotoCraftOnGold else colors.textSecondary,
            bordered = false,
        ) {
            PhotoCraftIcon(glyph = PhotoCraftGlyph.Export)
        }
    }
}

@Composable
private fun ToolbarIconButton(
    glyph: PhotoCraftGlyph,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    PhotoCraftIconButton(
        onClick = onClick,
        enabled = enabled,
        contentDescription = contentDescription,
        containerColor = Color.Transparent,
        contentColor = if (enabled) colors.textPrimary else colors.divider,
        bordered = false,
    ) {
        PhotoCraftIcon(glyph = glyph)
    }
}

@Composable
private fun FilterPanel(
    selectedFilter: PhotoFilter,
    onFilterSelected: (PhotoFilter) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PhotoCraftSpacing.xs),
    ) {
        PhotoCraftCategoryTabs(
            categories = PhotoFilter.entries.map { it.label },
            selectedCategory = selectedFilter.label,
            onCategorySelected = { label ->
                PhotoFilter.entries.firstOrNull { it.label == label }?.let(onFilterSelected)
            },
            contentPadding = PaddingValues(horizontal = PhotoCraftDimensions.screenPadding),
        )
        Text(
            text = "Presets are applied instantly and can be undone.",
            modifier = Modifier.padding(
                horizontal = PhotoCraftDimensions.screenPadding,
                vertical = PhotoCraftSpacing.sm,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun EmptyEditor(onOpenGallery: () -> Unit) {
    val colors = MaterialTheme.photoCraftColors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PhotoCraftSpacing.xxl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PhotoCraftIcon(
            glyph = PhotoCraftGlyph.Image,
            modifier = Modifier.size(PhotoCraftDimensions.iconLarge),
            tint = colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.lg))
        Text(
            text = "No photo selected",
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.xs))
        Text(
            text = "Pick a photo from your gallery to start editing.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(PhotoCraftSpacing.xl))
        PhotoCraftButton(onClick = onOpenGallery) {
            Text(text = "Open Gallery")
        }
    }
}

internal fun DrawScope.drawMask(mask: Mask) {
    val opacity = mask.opacity.coerceIn(0f, 1f)
    val minDimension = if (size.width < size.height) size.width else size.height
    when (mask.kind) {
        MaskKind.Brush -> {
            val radius = (mask.brushSize.coerceIn(0.005f, 0.5f) * minDimension).coerceAtLeast(1f)
            mask.geometry.brushPoints.forEach { point ->
                val alpha = maskAlpha(mask, point.x, point.y) * opacity
                val effectiveAlpha = if (mask.inverted) 1f - alpha else alpha
                if (mask.erased) return@forEach
                if (effectiveAlpha > 0.02f) {
                    drawCircle(
                        color = Color.White.copy(alpha = effectiveAlpha),
                        radius = radius,
                        center = Offset(size.width * point.x, size.height * point.y),
                    )
                }
            }
        }

        MaskKind.Radial -> {
            val center = mask.geometry.center ?: return
            val alpha = maskAlpha(mask, center.x, center.y) * opacity
            val effectiveAlpha = if (mask.inverted) 1f - alpha else alpha
            if (!mask.erased && effectiveAlpha > 0.02f) {
                val radius = (mask.geometry.radius * minDimension).coerceIn(
                    minDimension * 0.05f,
                    minDimension,
                )
                drawCircle(
                    color = Color.White.copy(alpha = effectiveAlpha),
                    radius = radius,
                    center = Offset(size.width * center.x, size.height * center.y),
                )
            }
        }

        MaskKind.Linear -> {
            val start = mask.geometry.start ?: return
            val end = mask.geometry.end ?: return
            val startAlpha = maskAlpha(mask, start.x, start.y) * opacity
            val endAlpha = maskAlpha(mask, end.x, end.y) * opacity
            val startEffective = if (mask.inverted) 1f - startAlpha else startAlpha
            val endEffective = if (mask.inverted) 1f - endAlpha else endAlpha
            if (mask.erased) return
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = startEffective.coerceIn(0f, 1f)),
                        Color.White.copy(alpha = endEffective.coerceIn(0f, 1f)),
                    ),
                    startY = size.height * (0.05f),
                    endY = size.height * (0.43f),
                ),
                size = size,
            )
        }
    }
}
