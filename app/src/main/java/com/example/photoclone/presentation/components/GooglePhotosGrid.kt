package com.example.photoclone.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
                bottom = if (isSelectionMode) 220.dp else 2.dp // Add padding when selection mode active
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

        // Selection action bar at bottom (like Google Photos)
        androidx.compose.animation.AnimatedVisibility(
            visible = showBottomSheet && selectedPhotos.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                SelectionBottomSheetContent(
                    selectedCount = selectedPhotos.size,
                    onClear = {
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onShare = {
                        // TODO: Implement share functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onAddToAlbum = {
                        // TODO: Implement add to album functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onCreate = {
                        // TODO: Implement create functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onDelete = {
                        // TODO: Implement delete functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onBackup = {
                        // TODO: Implement backup functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onArchive = {
                        // TODO: Implement archive functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
                    },
                    onMoveToLocked = {
                        // TODO: Implement locked folder functionality
                        selectedPhotos = emptySet()
                        showBottomSheet = false
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
 * Google Photos style with primary and expandable secondary actions
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
            .padding(bottom = 24.dp)
    ) {
        // Header with count and clear button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
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
                Text(
                    "Clear",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Primary actions - Google Photos style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Outlined.Share,
                label = "Share",
                onClick = onShare
            )
            ActionButton(
                icon = Icons.Outlined.Add,
                label = "Add",
                onClick = onAddToAlbum
            )
            ActionButton(
                icon = Icons.Outlined.Create,
                label = "Create",
                onClick = onCreate
            )
            ActionButton(
                icon = Icons.Outlined.Delete,
                label = "Delete",
                onClick = onDelete
            )
        }

        // Expandable more actions button
        TextButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (isExpanded) "Show less" else "More options",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        // Additional actions (expandable with animation)
        androidx.compose.animation.AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        icon = Icons.Outlined.CloudUpload,
                        label = "Backup",
                        onClick = onBackup
                    )
                    ActionButton(
                        icon = Icons.Outlined.Archive,
                        label = "Archive",
                        onClick = onArchive
                    )
                    ActionButton(
                        icon = Icons.Outlined.Lock,
                        label = "Lock",
                        onClick = onMoveToLocked
                    )
                }
            }
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Action button for selection bottom sheet
 * Google Photos style with circular icon button and label
 */
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
