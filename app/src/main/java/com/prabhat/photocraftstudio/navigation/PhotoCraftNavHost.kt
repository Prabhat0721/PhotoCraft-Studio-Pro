package com.prabhat.photocraftstudio.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.prabhat.photocraftstudio.core.data.PhotoRepository
import com.prabhat.photocraftstudio.feature.editor.CropRoute
import com.prabhat.photocraftstudio.feature.editor.EditorPresetHandoff
import com.prabhat.photocraftstudio.feature.editor.EditorRoute
import com.prabhat.photocraftstudio.feature.editor.MaskingRoute
import com.prabhat.photocraftstudio.feature.editor.PortraitRoute
import com.prabhat.photocraftstudio.feature.editor.PresetsRoute
import com.prabhat.photocraftstudio.feature.home.AlbumsRoute
import com.prabhat.photocraftstudio.feature.home.HomeRoute
import com.prabhat.photocraftstudio.ui.settings.SettingsRoute

@Composable
fun PhotoCraftNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = PhotoCraftDestination.GALLERY.route,
        modifier = modifier,
    ) {
        composable(
            route = PhotoCraftDestination.GALLERY.route,
            arguments = listOf(
                navArgument(PhotoCraftDestination.FILTER_ARGUMENT) {
                    type = NavType.StringType
                    defaultValue = PhotoCraftDestination.ALL_FILTER
                },
            ),
        ) { backStackEntry ->
            val filter = backStackEntry.arguments
                ?.getString(PhotoCraftDestination.FILTER_ARGUMENT)
                ?: PhotoCraftDestination.ALL_FILTER

            HomeRoute(
                initialFilter = filter,
                onPhotoClick = { photo ->
                    PhotoRepository.selectPhoto(photo)
                    navController.navigate(PhotoCraftDestination.EDITOR.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(route = PhotoCraftDestination.ALBUMS.route) {
            AlbumsRoute(
                onAlbumClick = { filter ->
                    navController.navigate(PhotoCraftDestination.galleryRoute(filter)) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(route = PhotoCraftDestination.PRESETS.route) {
            PresetsRoute(
                onOpenGallery = {
                    navController.navigate(
                        PhotoCraftDestination.galleryRoute(PhotoCraftDestination.ALL_FILTER),
                    ) {
                        launchSingleTop = true
                    }
                },
                onApplyPreset = { photo, filter ->
                    PhotoRepository.selectPhoto(photo)
                    EditorPresetHandoff.set(photo.id, filter)
                    navController.navigate(PhotoCraftDestination.EDITOR.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(route = PhotoCraftDestination.SETTINGS.route) {
            SettingsRoute()
        }

        composable(route = PhotoCraftDestination.EDITOR.route) {
            EditorRoute(
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(
                            PhotoCraftDestination.galleryRoute(PhotoCraftDestination.ALL_FILTER),
                        ) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenGallery = {
                    navController.navigate(
                        PhotoCraftDestination.galleryRoute(PhotoCraftDestination.ALL_FILTER),
                    ) {
                        popUpTo(PhotoCraftDestination.GALLERY.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onOpenCrop = {
                    navController.navigate(PhotoCraftDestination.CROP.route) {
                        launchSingleTop = true
                    }
                },
                onOpenMasking = {
                    navController.navigate(PhotoCraftDestination.MASKING.route) {
                        launchSingleTop = true
                    }
                },
                onOpenPortrait = {
                    navController.navigate(PhotoCraftDestination.PORTRAIT.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(route = PhotoCraftDestination.CROP.route) {
            CropRoute(
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(PhotoCraftDestination.EDITOR.route) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenGallery = {
                    navController.navigate(
                        PhotoCraftDestination.galleryRoute(PhotoCraftDestination.ALL_FILTER),
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(route = PhotoCraftDestination.MASKING.route) {
            MaskingRoute(
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(PhotoCraftDestination.EDITOR.route) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenGallery = {
                    navController.navigate(
                        PhotoCraftDestination.galleryRoute(PhotoCraftDestination.ALL_FILTER),
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(route = PhotoCraftDestination.PORTRAIT.route) {
            PortraitRoute(
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(PhotoCraftDestination.EDITOR.route) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenGallery = {
                    navController.navigate(
                        PhotoCraftDestination.galleryRoute(PhotoCraftDestination.ALL_FILTER),
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
