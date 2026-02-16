package com.example.photoclone.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Models for items shown in the bottom sheets of the app, such as the "Create new" and "Get photos" sheets.
 * Each item has an ID, title, optional subtitle and icon, and a flag to indicate if it's new. Sections group related items together under a common title.`
 * This structure allows for easy organization and display of bottom sheet content in the UI.
 * */

// Simple model for an item shown inside create/get bottom sheets.
data class BottomSheetItem(
    val id: Int,
    val title: String,
    val subtitle: String? = null, // optional small label under the title
    val icon: ImageVector? = null, // optional vector icon
    val isNew: Boolean = false // flag to show a 'New' badge
)

// Sections group related bottom-sheet items (title + items list).
sealed class BottomSheetSection(
    val title: String,
    val items: List<BottomSheetItem>
) {
    // "Create new" section with common creation actions
    data object CreateNew : BottomSheetSection(
        title = "Create new",
        items = listOf(
            BottomSheetItem(1, "Album", icon = Icons.Outlined.PhotoAlbum),
            BottomSheetItem(2, "Collage", icon = Icons.Outlined.Widgets),
            BottomSheetItem(3, "Highlight video", subtitle = "New", icon = Icons.Outlined.VideoLibrary, isNew = true),
            BottomSheetItem(4, "Cinematic photo", icon = Icons.Outlined.Camera),
            BottomSheetItem(5, "Animation", icon = Icons.Outlined.Animation),
            BottomSheetItem(6, "Remix", icon = Icons.Outlined.AutoFixHigh)
        )
    )

    // "Get photos" section with import/share actions
    data object GetPhotos : BottomSheetSection(
        title = "Get photos",
        items = listOf(
            BottomSheetItem(7, "Share with a partner", icon = Icons.Outlined.PersonAdd),
            BottomSheetItem(8, "Import from other places", icon = Icons.Outlined.FileDownload)
        )
    )
}