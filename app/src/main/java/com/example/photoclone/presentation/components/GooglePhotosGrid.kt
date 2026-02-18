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
import androidx.compose.ui.text.style.TextAlign
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
    var showBottomSheet by remember { mutableStateOf(false) }

    // Update selection mode and bottom sheet visibility
    LaunchedEffect(selectedPhotos.size) {
        isSelectionMode = selectedPhotos.isNotEmpty()
        showBottomSheet = selectedPhotos.isNotEmpty()
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

        // Modal Bottom Sheet for Selection Actions
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    selectedPhotos = emptySet()
                    showBottomSheet = false
                },
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                SelectionBottomSheetContent(
                    selectedCount = selectedPhotos.size,
                    onClear = {
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onShare = {
                        // TODO: Implement share functionality
                        // sharePhotos(selectedPhotos.map { photos[it] })
                    },
                    onAddToAlbum = {
                        // TODO: Implement add to album functionality
                        // showAlbumPicker(selectedPhotos.map { photos[it] })
                    },
                    onCreate = {
                        // TODO: Implement create functionality
                    },
                    onDelete = {
                        // TODO: Implement delete functionality
                        // deletePhotos(selectedPhotos.map { photos[it] })
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onBackup = {
                        // TODO: Implement backup functionality
                    },
                    onArchive = {
                        // TODO: Implement archive functionality
                    },
                    onMoveToLocked = {
                        // TODO: Implement locked folder functionality
                    }
                )
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

/**
 * Content for the Modal Bottom Sheet displaying selection actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionBottomSheetContent(
    selectedCount: Int,
    onClear: () -> Unit,
    onShare: () -> Unit,
    onAddToAlbum: () -> Unit,
    onCreate: () -> Unit,
    onDelete: () -> Unit,
    onBackup: () -> Unit,
    onArchive: () -> Unit,
    onMoveToLocked: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header with count and clear button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = onClear) {
                Text("Clear", fontWeight = FontWeight.Medium)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Primary actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Filled.Share,
                label = "Share",
                onClick = onShare
            )
            ActionButton(
                icon = Icons.Filled.Add,
                label = "Add",
                onClick = onAddToAlbum
            )
            ActionButton(
                icon = Icons.Filled.Delete,
                label = "Delete",
                onClick = onDelete
            )
        }

        // Expandable more actions button
        TextButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isExpanded) "Less options" else "More options")
        }

        // Additional actions (expandable)
        if (isExpanded) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Filled.CloudUpload,
                    label = "Backup",
                    onClick = onBackup
                )
                ActionButton(
                    icon = Icons.Filled.Archive,
                    label = "Archive",
                    onClick = onArchive
                )
                ActionButton(
                    icon = Icons.Filled.Lock,
                    label = "Lock",
                    onClick = onMoveToLocked
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
