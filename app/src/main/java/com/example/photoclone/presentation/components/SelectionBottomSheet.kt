package com.example.photoclone.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * Enhanced Selection Bottom Sheet (Google Photos Style)
 * Features:
 * - Snap to partially expanded state initially
 * - Drag up/down gestures
 * - Adaptive height based on actions
 * - Background dimming when active
 * - Smooth slide-out when cleared
 * - Hides bottom navigation when active
 */
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
    onMoveToLocked: () -> Unit,
    onBottomNavVisibilityChange: (Boolean) -> Unit = {}
) {
    // Sheet expansion states - use rememberSaveable to persist state
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Reset expansion when selection is cleared
    LaunchedEffect(selectedCount) {
        if (selectedCount == 0) {
            isExpanded = false
        }
    }

    // Calculate adaptive height based on actions count
    val baseHeight = 160.dp
    val expandedHeight = 280.dp
    val targetHeight = if (isExpanded) expandedHeight else baseHeight

    // Smooth height animation
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sheet_height"
    )

    // Background dimming alpha
    val dimAlpha by animateFloatAsState(
        targetValue = if (selectedCount > 0) 0.5f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "dim_alpha"
    )

    // Hide bottom navigation when selection is active
    LaunchedEffect(selectedCount) {
        onBottomNavVisibilityChange(selectedCount == 0)
    }

    // Back handler to clear selection
    if (selectedCount > 0) {
        BackHandler {
            onClear()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background dimming overlay
        if (selectedCount > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha))
                    .zIndex(1f)
            )
        }

        // Bottom Sheet with slide animation
        AnimatedVisibility(
            visible = selectedCount > 0,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .height(animatedHeight)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                // Snap to expanded or collapsed based on drag
                                if (dragOffset < -50) {
                                    isExpanded = true
                                } else if (dragOffset > 50) {
                                    isExpanded = false
                                }
                                dragOffset = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                dragOffset += dragAmount
                            }
                        )
                    }
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Drag handle with animation - more prominent
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(if (isExpanded) 56.dp else 48.dp)
                                .height(5.dp),
                            shape = RoundedCornerShape(3.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (isExpanded) 0.7f else 0.4f
                            )
                        ) {}
                    }

                    // Header: count + clear button
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(
                            onClick = onClear,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Clear",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Primary actions (always visible)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val btnContainer = MaterialTheme.colorScheme.surfaceVariant
                        val btnIconTint = MaterialTheme.colorScheme.onSurface

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
                            icon = Icons.Outlined.Delete,
                            label = "Delete",
                            onClick = onDelete,
                            containerColor = btnContainer,
                            contentColor = btnIconTint
                        )

                        AccessibleAction(
                            icon = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            label = if (isExpanded) "Less" else "More",
                            onClick = {
                                isExpanded = !isExpanded
                            },
                            containerColor = if (isExpanded)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                btnContainer,
                            contentColor = if (isExpanded)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                btnIconTint
                        )
                    }

                    // Expandable secondary actions
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = tween(durationMillis = 200)
                        ) + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val btnContainer = MaterialTheme.colorScheme.surfaceVariant
                                val btnIconTint = MaterialTheme.colorScheme.onSurface

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

                                AccessibleAction(
                                    icon = Icons.Outlined.Create,
                                    label = "Create",
                                    onClick = onCreate,
                                    containerColor = btnContainer,
                                    contentColor = btnIconTint
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun AccessibleAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
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
            modifier = Modifier.width(72.dp)
        )
    }
}
