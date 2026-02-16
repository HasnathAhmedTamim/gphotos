package com.example.photoclone.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Component for the bottom sheet that appears when photos are selected, showing available actions.
 * Each action is represented by an icon and a label, and triggers a callback when tapped.
 * */


// Bottom sheet with actions for selected photos.
data class PhotoAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoActionsBottomSheet(
    selectedCount: Int,
    onShare: () -> Unit,
    onAddToAlbum: () -> Unit,
    onCreate: () -> Unit,
    onDelete: () -> Unit,
    onBackup: () -> Unit,
    onArchive: () -> Unit,
    onDeleteFromArchive: () -> Unit,
    onEditLocation: () -> Unit,
    onMoveToLocked: () -> Unit,
    onDismiss: () -> Unit
) {
    // Define the available action buttons shown in the sheet.
    val actions = listOf(
        PhotoAction(Icons.Outlined.Share, "Share", onShare),
        PhotoAction(Icons.Outlined.Add, "Add to album", onAddToAlbum),
        PhotoAction(Icons.Outlined.Create, "Create", onCreate),
        PhotoAction(Icons.Outlined.Delete, "Bin", onDelete),
        PhotoAction(Icons.Outlined.CloudUpload, "Backup", onBackup),
        PhotoAction(Icons.Outlined.Archive, "Archive", onArchive),
        PhotoAction(Icons.Outlined.RemoveCircle, "Delete from archive", onDeleteFromArchive),
        PhotoAction(Icons.Outlined.LocationOn, "Edit location", onEditLocation),
        PhotoAction(Icons.Outlined.Lock, "Move to locked folder", onMoveToLocked)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            // Header showing number of selected items.
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Horizontally scrollable row of action buttons.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.forEach { action ->
                    PhotoActionButton(
                        icon = action.icon,
                        label = action.label,
                        onClick = {
                            action.onClick()
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PhotoActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    // Single circular icon button with a small label underneath.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp) // match the size of the FilledTonalIconButton so label width equals icon width
            .padding(vertical = 8.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
