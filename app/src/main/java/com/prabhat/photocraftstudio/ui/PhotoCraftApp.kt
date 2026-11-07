package com.prabhat.photocraftstudio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftDivider
import com.prabhat.photocraftstudio.core.designsystem.components.PhotoCraftIcon
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors
import com.prabhat.photocraftstudio.navigation.PhotoCraftDestination
import com.prabhat.photocraftstudio.navigation.PhotoCraftNavHost

@Composable
fun PhotoCraftApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val fullScreenRoutes = setOf(
        PhotoCraftDestination.EDITOR.route,
        PhotoCraftDestination.MASKING.route,
        PhotoCraftDestination.PORTRAIT.route,
        PhotoCraftDestination.CROP.route,
    )
    val showBottomBar = currentRoute !in fullScreenRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.photoCraftColors.background,
        bottomBar = {
            if (showBottomBar) {
                PhotoCraftBottomBar(
                    currentRoute = currentRoute,
                    onDestinationClick = { destination ->
                        navController.navigate(destination.navigationRoute()) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        PhotoCraftNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun PhotoCraftBottomBar(
    currentRoute: String?,
    onDestinationClick: (PhotoCraftDestination) -> Unit,
) {
    val colors = MaterialTheme.photoCraftColors

    Column {
        PhotoCraftDivider()
        NavigationBar(
            containerColor = colors.surface,
            tonalElevation = 0.dp,
        ) {
            PhotoCraftDestination.bottomBarDestinations.forEach { destination ->
                val selected = if (destination == PhotoCraftDestination.GALLERY) {
                    currentRoute?.startsWith("gallery") == true
                } else {
                    currentRoute == destination.route
                }

                NavigationBarItem(
                    selected = selected,
                    onClick = { onDestinationClick(destination) },
                    icon = {
                        PhotoCraftIcon(glyph = destination.glyph)
                    },
                    label = {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.gold,
                        selectedTextColor = colors.gold,
                        indicatorColor = colors.gold,
                        unselectedIconColor = colors.textSecondary,
                        unselectedTextColor = colors.textSecondary,
                    ),
                )
            }
        }
    }
}
