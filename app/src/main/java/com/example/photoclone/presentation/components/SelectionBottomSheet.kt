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
    // Use a compact baseHeight so the sheet doesn't cover too much screen when visible,
    // but still tall enough to show header + one row of icon+labels.
    val baseHeight = 160.dp
    val expandedHeight = 220.dp
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {

            // Larger handle for affordance
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 6.dp),
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

            // Header row: selected count + clear button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onClear, contentPadding = PaddingValues(8.dp)) {
                    Text(text = "Clear", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall)
                }
            }

            // Stronger divider
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Actions row with larger neutral circular buttons and clearer labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val btnContainer = MaterialTheme.colorScheme.surfaceVariant
                val btnIconTint = MaterialTheme.colorScheme.onSurface // stronger contrast

                // concise, consistent labels under each icon
                AccessibleAction(
                    icon = Icons.Outlined.Share,
                    label = "Share",
                    onClick = onShare,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                AccessibleAction(
                    icon = Icons.Outlined.Add,
                    label = "Add",
                    onClick = onAddToAlbum,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                AccessibleAction(
                    icon = Icons.Outlined.Create,
                    label = "Create",
                    onClick = onCreate,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                AccessibleAction(
                    icon = Icons.Outlined.Delete,
                    label = "Delete",
                    onClick = onDelete,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                AccessibleAction(
                    icon = Icons.Outlined.CloudUpload,
                    label = "Backup",
                    onClick = onBackup,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                AccessibleAction(
                    icon = Icons.Outlined.Archive,
                    label = "Archive",
                    onClick = onArchive,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                AccessibleAction(
                    icon = Icons.Outlined.Lock,
                    label = "Lock",
                    onClick = onMoveToLocked,
                    containerColor = btnContainer,
                    contentColor = btnIconTint
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Small bottom padding so the sheet doesn't feel tight
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AccessibleAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp) // match the Surface size so the label width equals the icon width
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = containerColor,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(72.dp) // explicitly match the icon width to avoid unexpected stretching/clipping
        )
    }
}
