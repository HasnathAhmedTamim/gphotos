package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.photoclone.presentation.components.DynamicBottomSheet

/**
 * Example 1: Photo Selection Bottom Sheet
 *
 * Use Case: When user selects photos in gallery
 * Trigger: selectionCount > 0
 *
 * Usage in your screen:
 * ```kotlin
 * PhotoSelectionSheet(
 *     selectedCount = selectedPhotos.size,
 *     isVisible = selectedPhotos.isNotEmpty(),
 *     onDismiss = { selectedPhotos.clear() },
 *     onShare = { sharePhotos(selectedPhotos) },
 *     onDelete = { deletePhotos(selectedPhotos) },
 *     onAddToAlbum = { showAlbumPicker() }
 * )
 * ```
 */
@Composable
fun PhotoSelectionSheet(
    selectedCount: Int,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onAddToAlbum: () -> Unit,
    onMore: () -> Unit = {}
) {
    DynamicBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        dragHandleContent = {
            // Custom header with count
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedCount selected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Clear")
                    }
                }
            }
        }
    ) {
        // Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SheetAction(
                icon = Icons.Outlined.Share,
                label = "Share",
                onClick = onShare
            )
            SheetAction(
                icon = Icons.Outlined.AddToPhotos,
                label = "Add to album",
                onClick = onAddToAlbum
            )
            SheetAction(
                icon = Icons.Outlined.Delete,
                label = "Delete",
                onClick = onDelete
            )
            SheetAction(
                icon = Icons.Outlined.MoreHoriz,
                label = "More options",
                onClick = onMore
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Example 2: Collection Options Bottom Sheet
 *
 * Use Case: When user long-presses a collection/album
 * Trigger: showCollectionOptions = true
 *
 * Usage:
 * ```kotlin
 * CollectionOptionsSheet(
 *     collectionName = collection.name,
 *     isVisible = showCollectionOptions,
 *     onDismiss = { showCollectionOptions = false },
 *     onRename = { showRenameDialog() },
 *     onShare = { shareCollection() },
 *     onDelete = { deleteCollection() }
 * )
 * ```
 */
@Composable
fun CollectionOptionsSheet(
    collectionName: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onAddPhotos: () -> Unit = {}
) {
    DynamicBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        dragHandleContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = collectionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SheetAction(
                icon = Icons.Outlined.DriveFileRenameOutline,
                label = "Rename",
                onClick = {
                    onRename()
                    onDismiss()
                }
            )
            SheetAction(
                icon = Icons.Outlined.AddPhotoAlternate,
                label = "Add photos",
                onClick = {
                    onAddPhotos()
                    onDismiss()
                }
            )
            SheetAction(
                icon = Icons.Outlined.Share,
                label = "Share album",
                onClick = {
                    onShare()
                    onDismiss()
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SheetAction(
                icon = Icons.Outlined.Delete,
                label = "Delete album",
                onClick = {
                    onDelete()
                    onDismiss()
                },
                isDestructive = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Example 3: Create Options Bottom Sheet
 *
 * Use Case: Floating button or toolbar action to create content
 * Trigger: User taps "+" button
 *
 * Usage:
 * ```kotlin
 * CreateOptionsSheet(
 *     isVisible = showCreateSheet,
 *     onDismiss = { showCreateSheet = false },
 *     onCreateAlbum = { navigateToCreateAlbum() },
 *     onCreateCollage = { navigateToCreateCollage() },
 *     onCreateAnimation = { navigateToCreateAnimation() }
 * )
 * ```
 */
@Composable
fun CreateOptionsSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCreateAlbum: () -> Unit,
    onCreateCollage: () -> Unit,
    onCreateAnimation: () -> Unit,
    onCreateMovie: () -> Unit = {}
) {
    DynamicBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Create new",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SheetAction(
                icon = Icons.Outlined.PhotoAlbum,
                label = "Album",
                description = "Group photos together",
                onClick = {
                    onCreateAlbum()
                    onDismiss()
                }
            )
            SheetAction(
                icon = Icons.Outlined.GridOn,
                label = "Collage",
                description = "Combine photos in a grid",
                onClick = {
                    onCreateCollage()
                    onDismiss()
                }
            )
            SheetAction(
                icon = Icons.Outlined.GifBox,
                label = "Animation",
                description = "Create animated GIF",
                onClick = {
                    onCreateAnimation()
                    onDismiss()
                }
            )
            SheetAction(
                icon = Icons.Outlined.Movie,
                label = "Movie",
                description = "Create video from photos",
                onClick = {
                    onCreateMovie()
                    onDismiss()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Example 4: Photo Info Bottom Sheet
 *
 * Use Case: Show photo details (like Google Photos info button)
 * Trigger: User taps info icon in photo viewer
 *
 * Usage:
 * ```kotlin
 * PhotoInfoSheet(
 *     isVisible = showPhotoInfo,
 *     onDismiss = { showPhotoInfo = false },
 *     photoName = photo.name,
 *     photoDate = photo.date,
 *     photoSize = photo.size,
 *     photoLocation = photo.location
 * )
 * ```
 */
@Composable
fun PhotoInfoSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    photoName: String,
    photoDate: String,
    photoSize: String,
    photoLocation: String? = null,
    photoResolution: String? = null
) {
    DynamicBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        skipPartiallyExpanded = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Photo details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoRow("Name", photoName)
            InfoRow("Date", photoDate)
            InfoRow("Size", photoSize)
            photoResolution?.let { InfoRow("Resolution", it) }
            photoLocation?.let { InfoRow("Location", it) }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Helper: Sheet Action Item
 */
@Composable
private fun SheetAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    description: String? = null,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDestructive)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Helper: Info Row
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
