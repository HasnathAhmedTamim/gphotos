package com.example.photoclone.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomSheetItem(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector? = null,
    val isNew: Boolean = false
)

sealed class BottomSheetSection(
    val title: String,
    val items: List<BottomSheetItem>
) {
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

    data object GetPhotos : BottomSheetSection(
        title = "Get photos",
        items = listOf(
            BottomSheetItem(7, "Share with a partner", icon = Icons.Outlined.PersonAdd),
            BottomSheetItem(8, "Import from other places", icon = Icons.Outlined.FileDownload)
        )
    )
}