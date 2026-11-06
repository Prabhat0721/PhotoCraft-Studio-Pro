package com.prabhat.photocraftstudio.feature.home

import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.data.PhotoSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun `gallery derives album filters and respects album selection`() {
        val photos = listOf(
            photo(id = "a", name = "Alpine", album = "Landscape", date = 3L),
            photo(id = "b", name = "City", album = "Street", date = 2L),
            photo(id = "c", name = "Forest", album = "Landscape", date = 1L, favorite = true),
        )

        val state = buildHomeUiState(
            allPhotos = photos,
            query = "",
            selectedFilter = "Landscape",
        )

        assertEquals(listOf("All", "Favorites", "Landscape", "Street"), state.filters)
        assertEquals(listOf("a", "c"), state.photos.map { it.id })
    }

    @Test
    fun `search matches photo names and albums`() {
        val photos = listOf(
            photo(id = "a", name = "Alpine Dawn", album = "Landscape", date = 3L),
            photo(id = "b", name = "Neon District", album = "Street", date = 2L),
        )

        val byName = buildHomeUiState(photos, query = "dawn", selectedFilter = "All")
        val byAlbum = buildHomeUiState(photos, query = "street", selectedFilter = "All")

        assertEquals(listOf("a"), byName.photos.map { it.id })
        assertEquals(listOf("b"), byAlbum.photos.map { it.id })
    }

    @Test
    fun `favorites filter shows only favourite photos`() {
        val photos = listOf(
            photo(id = "a", name = "Alpine", album = "Landscape", date = 3L),
            photo(id = "b", name = "City", album = "Street", date = 2L, favorite = true),
        )

        val state = buildHomeUiState(photos, query = "", selectedFilter = "Favorites")

        assertEquals(1, state.photos.size)
        assertTrue(state.photos.single().isFavorite)
    }

    private fun photo(
        id: String,
        name: String,
        album: String,
        date: Long,
        favorite: Boolean = false,
    ) = PhotoItem(
        id = id,
        name = name,
        source = PhotoSource.Resource(drawableId = 0),
        album = album,
        dateAddedMillis = date,
        isFavorite = favorite,
    )
}
