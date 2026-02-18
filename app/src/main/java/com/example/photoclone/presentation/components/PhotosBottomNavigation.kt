package com.example.photoclone.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Bottom navigation bar - Google Photos style
 */
@Composable
fun PhotosBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Photo, contentDescription = "Photos") },
            label = { Text("Photos") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Collections, contentDescription = "Collections") },
            label = { Text("Collections") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
    }
}

