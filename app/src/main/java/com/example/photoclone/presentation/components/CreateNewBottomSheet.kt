package com.example.photoclone.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.photoclone.presentation.model.CreateAction
import com.example.photoclone.presentation.model.CreateSection

/**
 * Google Photos-style "Create new" Modal Bottom Sheet
 *
 * Features:
 * - Modal overlay (tap outside to dismiss)
 * - Swipe down to dismiss
 * - Scrollable content
 * - Primary and secondary action sections
 * - "New" badges on items
 * - Back button handling
 *
 * Usage:
 * ```
 * var showCreateSheet by remember { mutableStateOf(false) }
 *
 * IconButton(onClick = { showCreateSheet = true }) {
 *     Icon(Icons.Default.Add, "Create")
 * }
 *
 * if (showCreateSheet) {
 *     CreateNewBottomSheet(
 *         onDismiss = { showCreateSheet = false },
 *         sections = createSections
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewBottomSheet(
    onDismiss: () -> Unit,
    sections: List<CreateSection>,
    modifier: Modifier = Modifier
) {
    // Handle back button - close sheet instead of exiting screen
    BackHandler(onBack = onDismiss)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        ),
        dragHandle = {
            // Custom drag handle with proper styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ) {}
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        CreateNewSheetContent(
            sections = sections,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun CreateNewSheetContent(
    sections: List<CreateSection>,
    onDismiss: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Title
        item {
            Text(
                text = "Create new",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
            )
        }

        // Render all sections
        sections.forEachIndexed { index, section ->
            // Section title (if provided)
            if (section.title != null) {
                item {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = if (index > 0) 8.dp else 0.dp,
                            bottom = 8.dp
                        )
                    )
                }
            }

            // Section actions
            items(section.actions) { action ->
                CreateActionItem(
                    action = action,
                    onDismiss = onDismiss
                )
            }

            // Divider between sections (except last)
            if (index < sections.lastIndex) {
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateActionItem(
    action: CreateAction,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                action.onClick()
                onDismiss() // Close sheet after action
            },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Title
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // "New" badge
            if (action.hasNewBadge) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "New",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
