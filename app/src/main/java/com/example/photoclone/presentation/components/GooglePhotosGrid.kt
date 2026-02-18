package com.example.photoclone.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Google Photos Style Grid with Modal Bottom Sheet
 * - Adaptive 3-column layout
 * - Multi-selection support with modal bottom sheet
 * - Date headers
 * - Selection actions in bottom sheet
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GooglePhotosGrid(
    photos: List<String>,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSelectionModeChange: (Boolean) -> Unit = {}
) {
    var selectedPhotos by remember { mutableStateOf(setOf<Int>()) }
    var isSelectionMode by remember { mutableStateOf(false) }

    // Update selection mode
    LaunchedEffect(selectedPhotos.size) {
        isSelectionMode = selectedPhotos.isNotEmpty()
        onSelectionModeChange(isSelectionMode)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Photo grid with date sections
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                start = 2.dp,
                end = 2.dp,
                top = 2.dp,
                bottom = 2.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Date header (Google Photos style)
            item(span = { GridItemSpan(3) }) {
                DateHeader("Today")
            }

            items(photos.size) { index ->
                GooglePhotoGridItem(
                    imageUrl = photos[index],
                    isSelected = selectedPhotos.contains(index),
                    isSelectionMode = isSelectionMode,
                    onClick = {
                        if (isSelectionMode) {
                            selectedPhotos = if (selectedPhotos.contains(index)) {
                                selectedPhotos - index
                            } else {
                                selectedPhotos + index
                            }
                        } else {
                            onPhotoClick(index)
                        }
                    },
                    onLongClick = {
                        if (!isSelectionMode) {
                            selectedPhotos = setOf(index)
                        }
                    }
                )
            }

            // Load more indicator
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GooglePhotoGridItem(
    imageUrl: String,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        // Photo
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(400, 400)
                .crossfade(true)
                .build(),
            contentDescription = "photo_item_${'$'}{imageUrl.hashCode()}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Selection overlay
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        else
                            Color.Black.copy(alpha = 0.1f)
                    )
            )

            // Checkbox
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

