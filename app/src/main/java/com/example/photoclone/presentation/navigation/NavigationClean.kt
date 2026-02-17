package com.example.photoclone.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoclone.presentation.screens.HomeScreenClean

/**
 * Clean navigation setup
 * - Simple routes
 * - No complex state
 * - Easy to maintain
 */
@Composable
fun NavigationClean() {
    val navController = rememberNavController()

    // Demo photos for testing
    val demoPhotos = List(30) { index ->
        "https://picsum.photos/1080/1920?seed=$index"
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreenClean(
                photos = demoPhotos,
                currentRoute = "home",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("collection") {
            HomeScreenClean(
                photos = demoPhotos,
                currentRoute = "collection",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("search") {
            HomeScreenClean(
                photos = demoPhotos,
                currentRoute = "search",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
