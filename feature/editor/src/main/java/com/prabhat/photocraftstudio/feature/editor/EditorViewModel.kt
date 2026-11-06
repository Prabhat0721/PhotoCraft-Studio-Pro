package com.prabhat.photocraftstudio.feature.editor

import androidx.lifecycle.ViewModel
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

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

    fun updateEffect(adjustment: EffectAdjustment, value: Float) = updateGesture {
        it.copy(effects = it.effects.withValue(adjustment, value))
    }

    fun updateDetail(adjustment: DetailAdjustment, value: Float) = updateGesture {
        it.copy(detail = it.detail.withValue(adjustment, value))
    }

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
