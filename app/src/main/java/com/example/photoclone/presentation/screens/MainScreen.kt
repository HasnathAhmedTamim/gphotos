package com.example.photoclone.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.presentation.components.PhotosBottomNavigation
import com.example.photoclone.presentation.viewmodel.PhotoViewModel

/**
 * Main app container with bottom navigation
 */
@Composable
fun MainScreen(
    viewModel: PhotoViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            PhotosBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> PhotosScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> SearchScreen(
                modifier = Modifier.padding(paddingValues)
            )
            2 -> CollectionsScreen(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
