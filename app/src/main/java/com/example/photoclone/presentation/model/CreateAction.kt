package com.example.photoclone.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an action in the "Create new" bottom sheet
 */
data class CreateAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val hasNewBadge: Boolean = false,
    val onClick: () -> Unit
)

/**
 * Section in the create sheet
 */
data class CreateSection(
    val title: String? = null,
    val actions: List<CreateAction>
)
