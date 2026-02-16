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
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoclone.R
import com.example.photoclone.presentation.components.CreateBottomSheetContent
import com.example.photoclone.presentation.model.BottomSheetItem
import com.example.photoclone.presentation.screens.CollectionScreen
import com.example.photoclone.presentation.screens.CreateScreen
import com.example.photoclone.presentation.screens.HomeScreen
import com.example.photoclone.presentation.screens.SearchScreen

/**
 *  Main navigation composable that sets up the NavHost and defines all the routes/screens.
 *  - Uses a single NavController for the entire app.
 *  - Manages the state of the bottom sheet at this level to ensure it can be triggered from any screen without issues.
 *  - Implements fade in/out transitions for screen navigation.
 *  - Handles navigation errors gracefully by logging them.
 *  - Uses string resources for bottom sheet item titles to support localization and prevent hardcoded strings.
 *  - Ensures that the bottom sheet state is not recreated on every recomposition, which can cause issues with the sheet's behavior.
 *  - The bottom sheet content is defined in a separate composable for better organization and reusability.
 *  - Each screen can trigger the bottom sheet by calling the onAddClick callback, which is passed down from this navigation composable.
 *
 * */
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
                onProfileClick = {
                    // TODO: Handle profile click
                },
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
                onProfileClick = {
                    // TODO: Handle profile click
                },
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
                }
            )
        }
    }

    // Create bottom sheet when requested from any screen
    if (showBottomSheet) {
        // Load localized strings once per composition
        val albumStr = stringResource(R.string.album)
        val collageStr = stringResource(R.string.collage)
        val highlightVideoStr = stringResource(R.string.highlight_video)
        val cinematicPhotoStr = stringResource(R.string.cinematic_photo)
        val animationStr = stringResource(R.string.animation)
        val remixStr = stringResource(R.string.remix)
        val sharePartnerStr = stringResource(R.string.share_with_partner)
        val importStr = stringResource(R.string.import_from_other_places)

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            CreateBottomSheetContent(
                onDismiss = { showBottomSheet = false },
                onItemClick = { item: BottomSheetItem ->
                    // Handle bottom sheet item clicks using localized titles
                    when (item.title) {
                        albumStr -> {
                            // TODO: Create album
                        }
                        collageStr -> {
                            // TODO: Create collage
                        }
                        highlightVideoStr -> {
                            // TODO: Create highlight video
                        }
                        cinematicPhotoStr -> {
                            // TODO: Create cinematic photo
                        }
                        animationStr -> {
                            // TODO: Create animation
                        }
                        remixStr -> {
                            // TODO: Create remix
                        }
                        sharePartnerStr -> {
                            // TODO: Share with partner
                        }
                        importStr -> {
                            // TODO: Import from other places
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
}