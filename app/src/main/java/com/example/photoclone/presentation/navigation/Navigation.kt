package com.example.photoclone.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.photoclone.presentation.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCloneNavigation() {
    val navController = rememberNavController()
    var showBottomSheet by remember { mutableStateOf(false) }

    val demoPhotos = List(30) { index ->
        "https://picsum.photos/400/400?seed=$index&blur=${index % 5}"
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
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
                    navController.navigate(route) {
                        // Avoid multiple copies of the same destination when reselecting same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }

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
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Screen.Create.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            // Create screen placeholder
            HomeScreen(
                photos = emptyList(),
                currentRoute = Screen.Create.route,
                onAddClick = { showBottomSheet = true },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Screen.Search.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            // Search screen placeholder
            HomeScreen(
                photos = demoPhotos.take(10),
                currentRoute = Screen.Search.route,
                onAddClick = { showBottomSheet = true },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

    // Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CreateBottomSheetContent(
                onDismiss = { showBottomSheet = false },
                onItemClick = { item: BottomSheetItem ->
                    // Handle bottom sheet item click based on the selected item
                    when (item.title) {
                        "Album" -> {
                            // TODO: Create album
                        }
                        "Collage" -> {
                            // TODO: Create collage
                        }
                        "Highlight video" -> {
                            // TODO: Create highlight video
                        }
                        "Cinematic photo" -> {
                            // TODO: Create cinematic photo
                        }
                        "Animation" -> {
                            // TODO: Create animation
                        }
                        "Remix" -> {
                            // TODO: Create remix
                        }
                        "Share with a partner" -> {
                            // TODO: Share with partner
                        }
                        "Import from other places" -> {
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