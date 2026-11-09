package com.prabhat.photocraftstudio.feature.editor

import androidx.lifecycle.ViewModel
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow

data class EditorUiState(
    val photo: PhotoItem? = null,
    val edit: EditState = EditState(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
)

class EditorViewModel : ViewModel() {

    private val editState = MutableStateFlow(EditState())
    private val canUndo = MutableStateFlow(false)
    private val canRedo = MutableStateFlow(false)
    private val presetStore = MutableStateFlow<List<PhotoFilter>>(listOf(PhotoFilter.Mono, PhotoFilter.Warm, PhotoFilter.Cool, PhotoFilter.Vintage))

    private val undoStack = ArrayDeque<EditState>()
    private val redoStack = ArrayDeque<EditState>()
    private var gestureStart: EditState? = null
    private var currentPhotoId: String? = null

    val uiState: Flow<EditorUiState> = combine(
        PhotoRepository.selectedPhoto,
        editState,
        canUndo,
        canRedo,
    ) { photo, edit, undoAvailable, redoAvailable ->
        EditorUiState(
            photo = photo,
            edit = edit,
            canUndo = undoAvailable,
            canRedo = redoAvailable,
        )
    }

    fun onPhotoChanged(photoId: String?) {
        if (currentPhotoId == photoId) return
        currentPhotoId = photoId
        clearHistory()
        editState.value = EditState()
    }

    fun updateLight(adjustment: LightAdjustment, value: Float) = updateGesture {
        it.copy(light = it.light.withValue(adjustment, value))
    }

    fun updateColor(adjustment: ColorAdjustment, value: Float) = updateGesture {
        it.copy(color = it.color.withValue(adjustment, value))
    }

    fun updateHsl(channel: HslChannel, component: HslComponent, value: Float) = updateGesture {
        it.copy(hsl = it.hsl.withValue(channel, component, value))
    }

    fun updateGradingWheel(scope: GradingScope, hue: Float, saturation: Float) = updateGesture {
        it.copy(grading = it.grading.withHueSaturation(scope, hue, saturation))
    }

    fun updateGradingLuminance(scope: GradingScope, value: Float) = updateGesture {
        it.copy(grading = it.grading.withLuminance(scope, value))
    }

    fun updateGradingBlend(value: Float) = updateGesture {
        it.copy(grading = it.grading.copy(blend = value))
    }

    fun updateCurve(channel: RgbChannel, x: Float, y: Float) = updateGesture { edit ->
        edit.copy(curves = edit.curves.withValue(channel, x, y))
    }

    fun updateEffect(adjustment: EffectAdjustment, value: Float) = updateGesture {
        it.copy(effects = it.effects.withValue(adjustment, value))
    }

    fun updateDetail(adjustment: DetailAdjustment, value: Float) = updateGesture {
        it.copy(detail = it.detail.withValue(adjustment, value))
    }

    fun updatePortrait(transform: (PortraitEnhancements) -> PortraitEnhancements) = commit { it.copy(portrait = transform(it.portrait)) }

    fun applyPendingPreset(photoId: String) {
        val filter = EditorPresetHandoff.consumeFor(photoId) ?: return
        commit { it.copy(filter = filter) }
    }

    fun finishGesture() {
        val start = gestureStart ?: return
        gestureStart = null
        if (start != editState.value) {
            pushUndo(start)
        }
    }

    fun setFilter(filter: PhotoFilter) = commit { it.copy(filter = filter) }

    val presets: StateFlow<List<PhotoFilter>> = presetStore.asStateFlow()

    fun savePreset(filter: PhotoFilter) {
        val current = presetStore.value
        if (current.any { it == filter }) return
        presetStore.value = current + filter
    }

    fun deletePreset(filter: PhotoFilter) {
        presetStore.value = presetStore.value.filter { it != filter }
    }

    fun addMask(kind: MaskKind) = commit { edit ->
        val mask = Mask(
            id = "mask_${edit.masks.size}_${System.currentTimeMillis()}",
            kind = kind,
            enabled = true,
        )
        edit.copy(masks = edit.masks + mask, selectedMaskId = mask.id)
    }

    fun updateMask(id: String, transform: (Mask) -> Mask) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) transform(it) else it })
    }

    fun duplicateMask(id: String) = commit { edit ->
        val source = edit.masks.firstOrNull { it.id == id }
        if (source == null) {
            edit
        } else {
            val copyMask = source.copy(
                id = "mask_${edit.masks.size}_${System.currentTimeMillis()}",
                geometry = source.geometry.copy(
                    brushPoints = source.geometry.brushPoints + source.geometry.brushPoints,
                ),
            )
            edit.copy(masks = edit.masks + copyMask, selectedMaskId = copyMask.id)
        }
    }

    fun deleteMask(id: String) = commit { edit ->
        val remaining = edit.masks.filterNot { it.id == id }
        edit.copy(masks = remaining, selectedMaskId = remaining.lastOrNull()?.id)
    }

    fun invertMask(id: String) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(inverted = !it.inverted) else it })
    }

    fun setMaskEnabled(id: String, enabled: Boolean) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(enabled = enabled) else it })
    }

    fun selectMask(id: String?) = commit { edit ->
        edit.copy(selectedMaskId = id)
    }

    fun updateMaskBrushSize(id: String, brushSize: Float) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(brushSize = brushSize) else it })
    }

    fun updateMaskFeather(id: String, feather: Float) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(feather = feather) else it })
    }

    fun updateMaskFlow(id: String, flow: Float) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(flow = flow) else it })
    }

    fun updateMaskOpacity(id: String, opacity: Float) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(opacity = opacity) else it })
    }

    fun updateMaskErase(id: String, erased: Boolean) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(erased = erased) else it })
    }

    fun updateMaskAdjustments(
        id: String,
        brightness: Float,
        contrast: Float,
        saturation: Float,
        temperature: Float,
    ) = commit { edit ->
        edit.copy(masks = edit.masks.map { if (it.id == id) it.copy(adjustments = MaskAdjustments(brightness, contrast, saturation, temperature)) else it })
    }

    fun rotateClockwise() = commit {
        it.copy(rotationQuarterTurns = (it.rotationQuarterTurns + 1) % 4)
    }

    fun toggleFlip() = commit { it.copy(flipHorizontal = !it.flipHorizontal) }

    fun reset() = commit { EditState() }

    fun undo() {
        val previous = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(editState.value)
        editState.value = previous
        syncHistoryFlags()
    }

    fun redo() {
        val next = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(editState.value)
        editState.value = next
        syncHistoryFlags()
    }

    private fun updateGesture(transform: (EditState) -> EditState) {
        if (gestureStart == null) {
            gestureStart = editState.value
        }
        editState.value = transform(editState.value)
    }

    private fun commit(transform: (EditState) -> EditState) {
        val previous = editState.value
        val next = transform(previous)
        if (next == previous) return
        gestureStart = null
        pushUndo(previous)
        editState.value = next
    }

    private fun pushUndo(previous: EditState) {
        undoStack.addLast(previous)
        if (undoStack.size > MAX_HISTORY) {
            undoStack.removeFirst()
        }
        redoStack.clear()
        syncHistoryFlags()
    }

    private fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
        gestureStart = null
        syncHistoryFlags()
    }

    private fun syncHistoryFlags() {
        canUndo.value = undoStack.isNotEmpty()
        canRedo.value = redoStack.isNotEmpty()
    }

    private companion object {
        const val MAX_HISTORY = 50
    }
}
