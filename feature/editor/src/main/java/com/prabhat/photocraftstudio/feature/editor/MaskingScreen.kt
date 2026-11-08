package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftAsyncImage
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButton
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftButtonVariant
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftCategoryTabs
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftCornerRadius
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftDimensions
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
fun MaskingRoute(
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel(),
) {
    val photo by PhotoRepository.selectedPhoto.collectAsStateWithLifecycle()

    MaskingScreen(
        photo = photo,
        viewModel = viewModel,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    )
}

@Composable
fun MaskingScreen(
    photo: PhotoItem?,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = EditorUiState())
    val edit = uiState.edit
    val selectedMask = edit.selectedMask()
    var selectedTypeIndex by rememberSaveable { mutableIntStateOf(0) }
    var feather by rememberSaveable { mutableFloatStateOf(0.42f) }
    var opacity by rememberSaveable { mutableFloatStateOf(0.72f) }
    var brushSize by rememberSaveable { mutableFloatStateOf(0.08f) }
    var flow by rememberSaveable { mutableFloatStateOf(1f) }
    val brushViewportSize = remember { mutableStateOf(IntSize(0, 0)) }
    val selectedType = MaskKind.entries.getOrElse(selectedTypeIndex) { MaskKind.Brush }

    EditorToolScaffold(
        title = "Masking",
        subtitle = "Guide the viewer with selective light and color.",
        badge = "Mask",
        photo = photo,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        modifier = modifier,
    ) { currentPhoto ->
        MaskWorkspace(
            photo = currentPhoto,
            edit = edit,
            selectedMask = selectedMask,
            selectedTypeIndex = selectedTypeIndex,
            feather = feather,
            opacity = opacity,
            brushSize = brushSize,
            flow = flow,
            onTypeSelected = { index ->
                selectedTypeIndex = index
                val kind = MaskKind.entries.getOrElse(index) { MaskKind.Brush }
                val mask = edit.selectedMask()
                if (mask != null) {
                    viewModel.updateMask(mask.id) { it.copy(kind = kind) }
                } else {
                    viewModel.addMask(kind)
                }
            },
            onFeatherChange = { value ->
                feather = value
                selectedMask?.let { viewModel.updateMaskFeather(it.id, value) }
            },
            onOpacityChange = { value ->
                opacity = value
                selectedMask?.let { viewModel.updateMaskOpacity(it.id, value) }
            },
            onBrushSizeChange = { value ->
                brushSize = value
                selectedMask?.let { viewModel.updateMaskBrushSize(it.id, value) }
            },
            onFlowChange = { value ->
                flow = value
                selectedMask?.let { viewModel.updateMaskFlow(it.id, value) }
            },
            onReset = {
                selectedTypeIndex = MaskKind.Brush.ordinal
                feather = 0.42f
                opacity = 0.72f
                brushSize = 0.08f
                flow = 1f
                selectedMask?.let { mask ->
                    viewModel.updateMask(mask.id) { it.copy(kind = MaskKind.Brush, inverted = false, erased = false, feather = 0.42f, flow = 1f, opacity = 0.72f, brushSize = 0.08f, geometry = MaskGeometry()) }
                }
            },
            onAddMask = { kind -> viewModel.addMask(kind) },
            onSelectMask = { id -> viewModel.selectMask(id) },
            onDuplicateMask = { id -> viewModel.duplicateMask(id) },
            onDeleteMask = { id -> viewModel.deleteMask(id) },
            onInvertMask = { id -> viewModel.invertMask(id) },
            onToggleMask = { id, enabled -> viewModel.setMaskEnabled(id, enabled) },
            onEraseMask = { id, erased -> viewModel.updateMaskErase(id, erased) },
            onBrushStroke = { id, points ->
                viewModel.updateMask(id) { mask -> mask.copy(geometry = mask.geometry.copy(brushPoints = points)) }
            },
            onAdjustmentsChange = { brightness, contrast, saturation, temperature ->
                selectedMask?.let { viewModel.updateMaskAdjustments(it.id, brightness, contrast, saturation, temperature) }
            },
        )
    }
}

@Composable
private fun MaskWorkspace(
    photo: PhotoItem,
    edit: EditState,
    selectedMask: Mask?,
    selectedTypeIndex: Int,
    feather: Float,
    opacity: Float,
    brushSize: Float,
    flow: Float,
    onTypeSelected: (Int) -> Unit,
    onFeatherChange: (Float) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onFlowChange: (Float) -> Unit,
    onReset: () -> Unit,
    onAddMask: (MaskKind) -> Unit,
    onSelectMask: (String) -> Unit,
    onDuplicateMask: (String) -> Unit,
    onDeleteMask: (String) -> Unit,
    onInvertMask: (String) -> Unit,
    onToggleMask: (String, Boolean) -> Unit,
    onEraseMask: (String, Boolean) -> Unit,
    onBrushStroke: (String, List<MaskPoint>) -> Unit,
    onAdjustmentsChange: (Float, Float, Float, Float) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors
    val activeKind = MaskKind.entries.getOrElse(selectedTypeIndex) { MaskKind.Brush }
    val brushViewportSize = remember { mutableStateOf(IntSize(0, 0)) }

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = PhotoCraftDimensions.screenPadding,
                    vertical = PhotoCraftSpacing.sm,
                )
                .clip(RoundedCornerShape(PhotoCraftCornerRadius.panel))
                .background(Color.Black),
        ) {
            PhotoCraftAsyncImage(
                model = photo.imageModel,
                contentDescription = photo.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                maxDimension = 1600,
                containerColor = Color.Black,
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates -> brushViewportSize.value = coordinates.size }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val mask = edit.selectedMask() ?: return@detectDragGestures
                            if (mask.kind != MaskKind.Brush) {
                                return@detectDragGestures
                            }
                            val viewport = brushViewportSize.value
                            val width = viewport.width.coerceAtLeast(1)
                            val height = viewport.height.coerceAtLeast(1)
                            onBrushStroke(
                                mask.id,
                                listOf(MaskPoint(x = change.position.x / width.toFloat(), y = change.position.y / height.toFloat())),
                            )
                        }
                    }
            ) {
                edit.masks.filter { it.enabled }.forEach { mask ->
                    drawMask(mask)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PhotoCraftDimensions.screenPadding,
                    vertical = PhotoCraftSpacing.sm,
                ),
            verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.sm),
        ) {
            PhotoCraftCategoryTabs(
                categories = MaskKind.entries.map { it.label },
                selectedCategory = activeKind.label,
                onCategorySelected = { label ->
                    MaskKind.entries.firstOrNull { it.label == label }?.let { kind ->
                        onTypeSelected(MaskKind.entries.indexOf(kind))
                    }
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = PhotoCraftDimensions.screenPadding),
            )

            Text(
                text = when (activeKind) {
                    MaskKind.Brush -> "Paint targeted adjustments over the skin or product area."
                    MaskKind.Linear -> "Blend an adjustment across a straight gradient."
                    MaskKind.Radial -> "Build a soft spotlight around the subject."
                },
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )

            ToolPercentSlider(
                label = "Feather",
                value = feather,
                onValueChange = onFeatherChange,
            )
            ToolPercentSlider(
                label = "Opacity",
                value = opacity,
                onValueChange = onOpacityChange,
            )
            if (activeKind == MaskKind.Brush) {
                ToolPercentSlider(
                    label = "Brush size",
                    value = brushSize,
                    onValueChange = onBrushSizeChange,
                )
                ToolPercentSlider(
                    label = "Flow",
                    value = flow,
                    onValueChange = onFlowChange,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${activeKind.label} selection",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                PhotoCraftButton(
                    onClick = onReset,
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Reset mask")
                }
            }

            MaskAdjustmentsSection(
                mask = selectedMask,
                onAdjustmentsChange = onAdjustmentsChange,
            )

            MaskListSection(
                masks = edit.masks,
                selectedMaskId = selectedMask?.id,
                onSelectMask = onSelectMask,
                onDuplicateMask = onDuplicateMask,
                onDeleteMask = onDeleteMask,
                onInvertMask = onInvertMask,
                onToggleMask = onToggleMask,
                onEraseMask = onEraseMask,
            )
        }
    }
}

@Composable
private fun MaskAdjustmentsSection(
    mask: Mask?,
    onAdjustmentsChange: (Float, Float, Float, Float) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    EditorPanelContainer(
        title = "Mask adjustments",
        subtitle = "Local values apply only inside the selected mask.",
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (mask == null) {
            Text(
                text = "Select or create a mask to apply local adjustments.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        } else {
            EditorAdjustmentSlider(
                label = "Brightness",
                value = mask.adjustments.brightness,
                onValueChange = { value ->
                    onAdjustmentsChange(value, mask.adjustments.contrast, mask.adjustments.saturation, mask.adjustments.temperature)
                },
                onValueChangeFinished = {},
            )
            EditorAdjustmentSlider(
                label = "Contrast",
                value = mask.adjustments.contrast,
                onValueChange = { value ->
                    onAdjustmentsChange(mask.adjustments.brightness, value, mask.adjustments.saturation, mask.adjustments.temperature)
                },
                onValueChangeFinished = {},
            )
            EditorAdjustmentSlider(
                label = "Saturation",
                value = mask.adjustments.saturation,
                onValueChange = { value ->
                    onAdjustmentsChange(mask.adjustments.brightness, mask.adjustments.contrast, value, mask.adjustments.temperature)
                },
                onValueChangeFinished = {},
            )
            EditorAdjustmentSlider(
                label = "Temperature",
                value = mask.adjustments.temperature,
                onValueChange = { value ->
                    onAdjustmentsChange(mask.adjustments.brightness, mask.adjustments.contrast, mask.adjustments.saturation, value)
                },
                onValueChangeFinished = {},
            )
        }
    }
}

@Composable
private fun MaskListSection(
    masks: List<Mask>,
    selectedMaskId: String?,
    onSelectMask: (String) -> Unit,
    onDuplicateMask: (String) -> Unit,
    onDeleteMask: (String) -> Unit,
    onInvertMask: (String) -> Unit,
    onToggleMask: (String, Boolean) -> Unit,
    onEraseMask: (String, Boolean) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xxs),
    ) {
        masks.forEach { mask ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mask.kind.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = if (mask.enabled) "Active" else "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                    )
                }
                PhotoCraftButton(
                    onClick = { onSelectMask(mask.id) },
                    modifier = Modifier.width(72.dp),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Select")
                }
                PhotoCraftButton(
                    onClick = { onDuplicateMask(mask.id) },
                    modifier = Modifier.width(72.dp),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Duplicate")
                }
                PhotoCraftButton(
                    onClick = { onInvertMask(mask.id) },
                    modifier = Modifier.width(72.dp),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Invert")
                }
                PhotoCraftButton(
                    onClick = { onDeleteMask(mask.id) },
                    modifier = Modifier.width(72.dp),
                    variant = PhotoCraftButtonVariant.Secondary,
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
