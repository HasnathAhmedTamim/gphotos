package com.example.photoclone.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Fully Reusable Dynamic Bottom Sheet
 *
 * Features:
 * - Works on ANY screen (Photos, Collections, Create, Settings, etc.)
 * - Supports collapsed/expanded states
 * - Gesture drag support (built-in via ModalBottomSheet)
 * - Can be linked to selection or screen state
 * - Automatic show/hide based on state
 * - Customizable drag handle
 * - Scrim (background dim) support
 *
 * Usage Examples:
 *
 * 1. Photo Selection Sheet:
 * ```kotlin
 * DynamicBottomSheet(
 *     isVisible = selectionCount > 0,
 *     onDismiss = { clearSelection() }
 * ) {
 *     PhotoActionsContent(selectedCount)
 * }
 * ```
 *
 * 2. Collection Options Sheet:
 * ```kotlin
 * DynamicBottomSheet(
 *     isVisible = showCollectionOptions,
 *     onDismiss = { showCollectionOptions = false }
 * ) {
 *     CollectionOptionsContent(collection)
 * }
 * ```
 *
 * 3. Settings Sheet:
 * ```kotlin
 * DynamicBottomSheet(
 *     isVisible = showSettingsSheet,
 *     onDismiss = { showSettingsSheet = false },
 *     showDragHandle = false
 * ) {
 *     SettingsContent()
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = true,
    skipPartiallyExpanded: Boolean = false,
    scrimColor: Color = Color.Transparent, // Transparent to allow touches through
    containerColor: Color = MaterialTheme.colorScheme.surface,
    dragHandleContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // Sheet state
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    // Auto show/hide based on isVisible
    LaunchedEffect(isVisible) {
        if (isVisible && !sheetState.isVisible) {
            sheetState.show()
        } else if (!isVisible && sheetState.isVisible) {
            sheetState.hide()
        }
    }

    // Back handler
    BackHandler(enabled = sheetState.isVisible) {
        onDismiss()
    }

    // Show sheet only when visible
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
            containerColor = containerColor,
            scrimColor = scrimColor,
            dragHandle = if (showDragHandle) {
                dragHandleContent ?: {
                    DefaultDragHandle()
                }
            } else null,
            content = content
        )
    }
}

/**
 * Default drag handle (Material 3 style)
 */
@Composable
private fun DefaultDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp),
            shape = RoundedCornerShape(2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        ) {}
    }
}

/**
 * Variant: Bottom Sheet with State Control
 *
 * This variant gives you direct control over the sheet state
 * for more complex scenarios (manual expand/collapse, etc.)
 *
 * Usage:
 * ```kotlin
 * val sheetState = rememberModalBottomSheetState()
 *
 * ControlledBottomSheet(
 *     sheetState = sheetState,
 *     onDismiss = { ... }
 * ) {
 *     // Your content
 * }
 *
 * // Manually control:
 * LaunchedEffect(someCondition) {
 *     sheetState.show()
 *     // or
 *     sheetState.hide()
 *     // or
 *     sheetState.expand()
 *     // or
 *     sheetState.partialExpand()
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlledBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showDragHandle: Boolean = true,
    scrimColor: Color = Color.Black.copy(alpha = 0.32f),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    BackHandler(enabled = sheetState.isVisible) {
        onDismiss()
    }

    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier,
            containerColor = containerColor,
            scrimColor = scrimColor,
            dragHandle = if (showDragHandle) {
                { DefaultDragHandle() }
            } else null,
            content = content
        )
    }
}

/**
 * Utility: Remember sheet visibility state
 *
 * Usage:
 * ```kotlin
 * val sheetVisible by rememberSheetVisibility(
 *     trigger = selectedPhotos.isNotEmpty()
 * )
 *
 * DynamicBottomSheet(
 *     isVisible = sheetVisible,
 *     onDismiss = { clearSelection() }
 * ) { ... }
 * ```
 */
@Composable
fun rememberSheetVisibility(trigger: Boolean): State<Boolean> {
    return remember { mutableStateOf(trigger) }.apply {
        LaunchedEffect(trigger) {
            value = trigger
        }
    }
}
