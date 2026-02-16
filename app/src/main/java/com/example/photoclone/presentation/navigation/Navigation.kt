package com.example.photoclone.presentation.navigation

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoclone.presentation.components.CreateBottomSheetContent
import com.example.photoclone.presentation.model.BottomSheetItem
import com.example.photoclone.presentation.screens.CollectionScreen
import com.example.photoclone.presentation.screens.CreateScreen
import com.example.photoclone.presentation.screens.HomeScreen
import com.example.photoclone.presentation.screens.SearchScreen
import com.example.photoclone.presentation.screens.ProfileScreen

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
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            HomeScreen(
                photos = demoPhotos,
                currentRoute = Screen.Home.route,
                onAddClick = { showBottomSheet = true },
                onNotificationClick = {
                    // TODO: Handle notification click
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNavigate = { route ->
                    // Safe navigation with try/catch
                    try {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        Log.e("Navigation", "Failed to navigate to $route", e)
                    }
                }
            )
        }

        // Collection screen route
        composable(
            route = Screen.Collection.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            CollectionScreen(
                currentRoute = Screen.Collection.route,
                onAddClick = { showBottomSheet = true },
                onNotificationClick = {
                    // TODO: Handle notification click
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNavigate = { route ->
                    try {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        Log.e("Navigation", "Failed to navigate to $route", e)
                    }
                }
            )
        }

        // Create screen route
        composable(
            route = Screen.Create.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            CreateScreen(
                currentRoute = Screen.Create.route,
                onAddClick = { showBottomSheet = true },
                onNotificationClick = {
                    // TODO: Handle notification click
                },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNavigate = { route ->
                    try {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        Log.e("Navigation", "Failed to navigate to $route", e)
                    }
                },
                onCreateClick = {
                    showBottomSheet = true
                }
            )
        }

        // Search screen route
        composable(
            route = Screen.Search.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SearchScreen(
                photos = demoPhotos,
                currentRoute = Screen.Search.route,
                onNavigate = { route ->
                    try {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        Log.e("Navigation", "Failed to navigate to $route", e)
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