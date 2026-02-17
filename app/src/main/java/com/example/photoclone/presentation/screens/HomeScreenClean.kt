package com.example.photoclone.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photoclone.presentation.components.PhotoPagerClean

/**
 * Clean HomeScreen implementation
 * - Simple state management
 * - Clear responsibilities
 * - No complex logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenClean(
    photos: List<String>,
    currentRoute: String,
    onNavigate: (String) -> Unit = {}
) {
    var showPager by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photos") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Search, "Search")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.MoreVert, "More")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { onNavigate("home") },
                    icon = { Icon(Icons.Filled.PhotoLibrary, "Photos") },
                    label = { Text("Photos") }
                )
                NavigationBarItem(
                    selected = currentRoute == "collection",
                    onClick = { onNavigate("collection") },
                    icon = { Icon(Icons.Filled.Collections, "Collections") },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = currentRoute == "search",
                    onClick = { onNavigate("search") },
                    icon = { Icon(Icons.Filled.Search, "Search") },
                    label = { Text("Search") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (showPager) {
                // Full-screen photo viewer
                PhotoPagerClean(
                    photos = photos,
                    initialPage = selectedIndex,
                    onDismiss = { showPager = false }
                )
            } else {
                // Photo grid
                PhotoGrid(
                    photos = photos,
                    onPhotoClick = { index ->
                        selectedIndex = index
                        showPager = true
                    }
                )
            }
        }
    }
}

@Composable
private fun PhotoGrid(
    photos: List<String>,
    onPhotoClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(photos.size) { index ->
            PhotoGridItem(
                imageUrl = photos[index],
                onClick = { onPhotoClick(index) }
            )
        }
    }
}

@Composable
private fun PhotoGridItem(
    imageUrl: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(400, 400)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    )
}
