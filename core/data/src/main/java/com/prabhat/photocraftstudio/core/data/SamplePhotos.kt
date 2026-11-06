package com.prabhat.photocraftstudio.core.data

private const val SAMPLE_DATE = 1_735_689_600_000L
private const val DAY_MILLIS = 86_400_000L

private fun samplePhoto(
    id: String,
    name: String,
    drawableId: Int,
    album: String,
    dayOffset: Int,
    favorite: Boolean = false,
) = PhotoItem(
    id = id,
    name = name,
    source = PhotoSource.Resource(drawableId),
    album = album,
    dateAddedMillis = SAMPLE_DATE - dayOffset * DAY_MILLIS,
    isFavorite = favorite,
)

internal fun samplePhotos(): List<PhotoItem> = listOf(
    samplePhoto(
        id = "sample_alpine",
        name = "Alpine Dawn",
        drawableId = R.drawable.sample_alpine,
        album = "Landscape",
        dayOffset = 1,
        favorite = true,
    ),
    samplePhoto(
        id = "sample_portrait",
        name = "Quiet Portrait",
        drawableId = R.drawable.sample_portrait,
        album = "Portrait",
        dayOffset = 2,
    ),
    samplePhoto(
        id = "sample_city",
        name = "Neon District",
        drawableId = R.drawable.sample_city,
        album = "Street",
        dayOffset = 3,
        favorite = true,
    ),
    samplePhoto(
        id = "sample_desert",
        name = "Dune Light",
        drawableId = R.drawable.sample_desert,
        album = "Landscape",
        dayOffset = 4,
    ),
    samplePhoto(
        id = "sample_forest",
        name = "Forest Mist",
        drawableId = R.drawable.sample_forest,
        album = "Nature",
        dayOffset = 5,
    ),
    samplePhoto(
        id = "sample_ocean",
        name = "Ocean Study",
        drawableId = R.drawable.sample_ocean,
        album = "Nature",
        dayOffset = 6,
        favorite = true,
    ),
    samplePhoto(
        id = "sample_bloom",
        name = "Golden Bloom",
        drawableId = R.drawable.sample_bloom,
        album = "Macro",
        dayOffset = 7,
    ),
    samplePhoto(
        id = "sample_architecture",
        name = "Concrete Lines",
        drawableId = R.drawable.sample_architecture,
        album = "Architecture",
        dayOffset = 8,
    ),
)
