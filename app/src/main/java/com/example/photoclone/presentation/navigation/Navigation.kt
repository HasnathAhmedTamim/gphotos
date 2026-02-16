@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNUSED_VALUE", "UNUSED_VARIABLE")

package com.example.photoclone.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.photoclone.presentation.components.CreateBottomSheetContent
import com.example.photoclone.presentation.model.BottomSheetItem
import com.example.photoclone.presentation.screens.CollectionScreen
import com.example.photoclone.presentation.screens.CreateScreen
import com.example.photoclone.presentation.screens.HomeScreen
import com.example.photoclone.presentation.screens.SearchScreen
import com.example.photoclone.presentation.screens.ProfileScreen
import androidx.navigation.compose.rememberNavController

/**
 *  Main navigation composable that sets up the NavHost and defines all the routes/screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCloneNavigation() {
    // Single NavController for app navigation
    val navController = rememberNavController()

    // Local UI state to show/hide the create bottom sheet
    var showBottomSheet by remember { mutableStateOf(false) }

    // Keep the sheet state remembered outside the conditional so it survives recompositions
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Demo photo URLs used for previewing the grids in the sample screens
    val demoPhotos = List(30) { index ->
        "https://picsum.photos/400/400?seed=$index&blur=${index % 5}"
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home screen route
        composable(route = Screen.Home.route) {
            HomeScreen(
                photos = demoPhotos,
                currentRoute = Screen.Home.route,
                onAddClick = { showBottomSheet = true },
                onNotificationClick = {
                    // TODO: Handle notification click
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNavigate = { route ->
                    // Direct navigation; NavController navigate is safe here
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Collection screen route
        composable(route = Screen.Collection.route) {
            CollectionScreen(
                currentRoute = Screen.Collection.route,
                onAddClick = { showBottomSheet = true },
                onNotificationClick = {
                    // TODO: Handle notification click
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Create screen route
        composable(route = Screen.Create.route) {
            CreateScreen(
                currentRoute = Screen.Create.route,
                onAddClick = { showBottomSheet = true },
                onNotificationClick = {
                    // TODO: Handle notification click
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCreateClick = {
                    showBottomSheet = true
                }
            )
        }

        // Search screen route
        composable(route = Screen.Search.route) {
            SearchScreen(
                photos = demoPhotos,
                currentRoute = Screen.Search.route,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        // Profile screen route
        composable(route = Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }

    // Touch the showBottomSheet state in a small effect so static analysis sees it being read.
    LaunchedEffect(showBottomSheet) {
        // no-op: this ensures the property is observed by analysis tools
    }

    // Create bottom sheet when requested from any screen
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            CreateBottomSheetContent(
                onDismiss = { showBottomSheet = false },
                onItemClick = { item: BottomSheetItem ->
                    // Handle bottom sheet item clicks using stable ids
                    when (item.id) {
                        1 -> {
                            // TODO: Create album
                        }
                        2 -> {
                            // TODO: Create collage
                        }
                        3 -> {
                            // TODO: Create highlight video
                        }
                        4 -> {
                            // TODO: Create cinematic photo
                        }
                        5 -> {
                            // TODO: Create animation
                        }
                        6 -> {
                            // TODO: Create remix
                        }
                        7 -> {
                            // TODO: Share with partner
                        }
                        8 -> {
                            // TODO: Import from other places
                        }
                        else -> {
                            // Unknown action
                        }
                    }
                    showBottomSheet = false
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Collection : Screen("collection")
    object Create : Screen("create")
    object Search : Screen("search")
    object Profile : Screen("profile")
}