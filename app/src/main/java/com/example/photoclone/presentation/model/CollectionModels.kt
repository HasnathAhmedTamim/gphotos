package com.example.photoclone.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an album in the Collections screen
 */
data class AlbumItem(
    val id: String,
    val title: String,
    val itemCount: Int,
    val thumbnailUrl: String? = null,
    val thumbnailRes: Int? = null
)

/**
 * Represents a category in the Collections screen
 */
data class CategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val route: String? = null
)
