package com.example.photoclone.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.presentation.components.GooglePhotosGrid
import com.example.photoclone.presentation.components.GooglePhotosViewer
import com.example.photoclone.presentation.viewmodel.PhotoViewModel

/**
 * Main photos screen with grid and viewer
 * Uses GooglePhotosGrid and GooglePhotosViewer components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showViewer by remember { mutableStateOf(false) }
    var viewerInitialPage by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photos") },
                actions = {
                    IconButton(onClick = { /* TODO: Show menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                uiState.photos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No photos found")
                    }
                }

                else -> {
                    // Convert Photo objects to image URLs for GooglePhotosGrid
                    val photoUrls = uiState.photos.map { it.imageUrl }

                    GooglePhotosGrid(
                        photos = photoUrls,
                        onPhotoClick = { index ->
                            viewerInitialPage = index
                            showViewer = true
                        },
                        onSelectionModeChange = { /* Handle selection mode */ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Photo viewer overlay
            if (showViewer && uiState.photos.isNotEmpty()) {
                GooglePhotosViewer(
                    photos = uiState.photos.map { it.imageUrl },
                    initialPage = viewerInitialPage,
                    onDismiss = { showViewer = false }
                )
            }
        }
    }
}
