package com.example.photoclone.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
/**
 * Composable function for the bottom sheet that appears when photos are selected, showing available actions.
 * The sheet expands in height when items are selected to accommodate the action buttons, and collapses when no items are selected.
 * Each action is represented by an icon and a label, and triggers a callback when tapped.
 * @param selectedCount The number of photos currently selected, used to determine the height of the bottom sheet and the header text.
 * @param onClear Callback to be invoked when the user clicks the "Clear" button to deselect all photos.
 * @param onShare Callback to be invoked when the user clicks the "Share" action button.
 * @param onAddToAlbum Callback to be invoked when the user clicks the "Add to album" action button.
 * @param onCreate Callback to be invoked when the user clicks the "Create" action button.
 * @param onDelete Callback to be invoked when the user clicks the "Bin" action button.
 * @param onBackup Callback to be invoked when the user clicks the "Backup" action button.
 * @param onArchive Callback to be invoked when the user clicks the "Archive" action button.
 * @param onMoveToLocked Callback to be invoked when the user clicks the "Move to locked folder" action button.
 *
 * */
// Bottom sheet shown when photos are selected; provides bulk actions.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionBottomSheet(
    selectedCount: Int,
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
    onShare: () -> Unit,
    onAddToAlbum: () -> Unit,
    onCreate: () -> Unit,
    onDelete: () -> Unit,
    onBackup: () -> Unit,
    onArchive: () -> Unit,
    onMoveToLocked: () -> Unit
) {
    // Base and expanded heights control sheet size when items are selected.
    val baseHeight = 180.dp // small collapsed height
    val expandedHeight = 240.dp // taller when items selected
    val targetHeight = if (selectedCount > 0) expandedHeight else baseHeight
    val height by animateDpAsState(targetValue = targetHeight)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 18.dp
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)) {

            // Drag handle for affordance
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f))
                )
            }

            // Header row: shows how many are selected and a Clear action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onClear, contentPadding = PaddingValues(10.dp)) {
                    Text(text = "Clear", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall)
                }
            }

            // Strong visual divider
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Horizontally scrollable actions row with circular buttons and labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val btnContainer = MaterialTheme.colorScheme.surfaceVariant
                val btnIconTint = MaterialTheme.colorScheme.onSurface // stronger contrast

                // Each action uses the IconLabelButton directly
                IconLabelButton(
                    icon = Icons.Outlined.Share,
                    label = "Share",
                    onClick = onShare,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                IconLabelButton(
                    icon = Icons.Outlined.Add,
                    label = "Add",
                    onClick = onAddToAlbum,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                IconLabelButton(
                    icon = Icons.Outlined.Create,
                    label = "Create",
                    onClick = onCreate,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                IconLabelButton(
                    icon = Icons.Outlined.Delete,
                    label = "Delete",
                    onClick = onDelete,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                IconLabelButton(
                    icon = Icons.Outlined.CloudUpload,
                    label = "Backup",
                    onClick = onBackup,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                IconLabelButton(
                    icon = Icons.Outlined.Archive,
                    label = "Archive",
                    onClick = onArchive,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                IconLabelButton(
                    icon = Icons.Outlined.Lock,
                    label = "Lock",
                    onClick = onMoveToLocked,
                    size = 72.dp,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                Spacer(modifier = Modifier.width(8.dp))

            }

            // Flexible spacer so sheet content aligns nicely when expanded
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
