package com.prabhat.photocraftstudio.navigation

import android.net.Uri
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftGlyph

enum class PhotoCraftDestination(
    val route: String,
    val label: String,
    val glyph: PhotoCraftGlyph,
) {
    GALLERY("gallery?filter={filter}", "Gallery", PhotoCraftGlyph.Grid),
    ALBUMS("albums", "Albums", PhotoCraftGlyph.Albums),
    PRESETS("presets", "Presets", PhotoCraftGlyph.Presets),
    EDITOR("editor", "Editor", PhotoCraftGlyph.Tune),
    SETTINGS("settings", "Settings", PhotoCraftGlyph.Settings),
    MASKING("masking", "Masking", PhotoCraftGlyph.Mask),
    PORTRAIT("portrait", "Portrait", PhotoCraftGlyph.Portrait),
    CROP("crop", "Crop", PhotoCraftGlyph.Crop),
    ;

    fun navigationRoute(): String = when (this) {
        GALLERY -> galleryRoute(ALL_FILTER)
        ALBUMS,
        PRESETS,
        EDITOR,
        SETTINGS,
        MASKING,
        PORTRAIT,
        CROP -> route
    }

    fun showsBottomBar(): Boolean = this in bottomBarDestinations

    companion object {
        const val FILTER_ARGUMENT = "filter"
        const val ALL_FILTER = "All"

        val bottomBarDestinations = listOf(GALLERY, ALBUMS, PRESETS, EDITOR, SETTINGS)

        fun galleryRoute(filter: String): String = "gallery?filter=${Uri.encode(filter)}"
    }
}
