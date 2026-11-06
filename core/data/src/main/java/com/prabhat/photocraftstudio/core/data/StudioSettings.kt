package com.prabhat.photocraftstudio.core.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class GalleryDensity(
    val label: String,
    val minTileSizeDp: Int,
) {
    Comfortable("Comfortable", 140),
    Compact("Compact", 104),
}

data class StudioSettings(
    val galleryDensity: GalleryDensity = GalleryDensity.Comfortable,
    val showPhotoLabels: Boolean = true,
    val keepScreenOnInEditor: Boolean = false,
)

class AppSettingsRepository private constructor(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<StudioSettings> = _settings.asStateFlow()

    fun setGalleryDensity(density: GalleryDensity) {
        preferences.edit().putString(KEY_GALLERY_DENSITY, density.name).apply()
        _settings.update { it.copy(galleryDensity = density) }
    }

    fun setShowPhotoLabels(show: Boolean) {
        preferences.edit().putBoolean(KEY_SHOW_PHOTO_LABELS, show).apply()
        _settings.update { it.copy(showPhotoLabels = show) }
    }

    fun setKeepScreenOnInEditor(keepOn: Boolean) {
        preferences.edit().putBoolean(KEY_KEEP_SCREEN_ON, keepOn).apply()
        _settings.update { it.copy(keepScreenOnInEditor = keepOn) }
    }

    fun reset() {
        preferences.edit().clear().apply()
        _settings.value = StudioSettings()
    }

    private fun readSettings(): StudioSettings {
        val density = preferences.getString(KEY_GALLERY_DENSITY, null)
            ?.let { stored -> GalleryDensity.entries.firstOrNull { it.name == stored } }
            ?: GalleryDensity.Comfortable
        return StudioSettings(
            galleryDensity = density,
            showPhotoLabels = preferences.getBoolean(KEY_SHOW_PHOTO_LABELS, true),
            keepScreenOnInEditor = preferences.getBoolean(KEY_KEEP_SCREEN_ON, false),
        )
    }

    companion object {
        private const val PREFERENCES_NAME = "photocraft_studio_settings"
        private const val KEY_GALLERY_DENSITY = "gallery_density"
        private const val KEY_SHOW_PHOTO_LABELS = "show_photo_labels"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"

        @Volatile
        private var instance: AppSettingsRepository? = null

        fun get(context: Context): AppSettingsRepository =
            instance ?: synchronized(this) {
                instance ?: AppSettingsRepository(context).also { instance = it }
            }
    }
}
