package com.example.photoclone.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoclone.presentation.screens.CollectionsScreen
import com.example.photoclone.presentation.screens.CreateScreenNew
import com.example.photoclone.presentation.screens.GooglePhotosHomeScreen

/**
 * Google Photos 4-Tab Navigation (2025-2026 UI)
 * Photos | Collections | Create | Search
 */
@Composable
fun GooglePhotosNavigation() {
    val navController = rememberNavController()

    // High-quality demo photos (1920x1080)
    val demoPhotos = List(50) { index ->
        when (index % 5) {
            0 -> "https://picsum.photos/1920/1080?seed=$index&nature"
            1 -> "https://picsum.photos/1080/1920?seed=$index&portrait"
            2 -> "https://picsum.photos/1920/1080?seed=$index&landscape"
            3 -> "https://picsum.photos/1080/1920?seed=$index&city"
            else -> "https://picsum.photos/1920/1080?seed=$index&random"
        }
    }

    NavHost(
        navController = navController,
        startDestination = "photos"
    ) {
        composable("photos") {
            GooglePhotosHomeScreen(
                photos = demoPhotos,
                currentRoute = "photos",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("collections") {
            CollectionsScreen(
                currentRoute = "collections",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("create") {
            CreateScreenNew(
                currentRoute = "create",
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable("search") {
            GooglePhotosHomeScreen(
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
