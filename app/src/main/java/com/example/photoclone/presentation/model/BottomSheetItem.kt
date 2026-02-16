package com.example.photoclone.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.photoclone.R

/**
 * Models for items shown in the bottom sheets of the app, such as the "Create new" and "Get photos" sheets.
 * Each item has an ID, title resource id, optional subtitle resource id and icon, and a flag to indicate if it's new.
 */

// Simple model for an item shown inside create/get bottom sheets.
data class BottomSheetItem(
    val id: Int,
    val titleRes: Int,
    val subtitleRes: Int? = null, // optional subtitle as string resource id
    val icon: ImageVector? = null,
    val isNew: Boolean = false
)

// Sections group related bottom-sheet items (titleRes + items list).
sealed class BottomSheetSection(
    val titleRes: Int,
    val items: List<BottomSheetItem>
) {
    // "Create new" section with common creation actions
    data object CreateNew : BottomSheetSection(
        titleRes = R.string.create,
        items = listOf(
            BottomSheetItem(1, R.string.album, icon = Icons.Outlined.PhotoAlbum),
            BottomSheetItem(2, R.string.collage, icon = Icons.Outlined.Widgets),
            BottomSheetItem(3, R.string.highlight_video, subtitleRes = R.string.badge_new, icon = Icons.Outlined.VideoLibrary, isNew = true),
            BottomSheetItem(4, R.string.cinematic_photo, icon = Icons.Outlined.Camera),
            BottomSheetItem(5, R.string.animation, icon = Icons.Outlined.Animation),
            BottomSheetItem(6, R.string.remix, icon = Icons.Outlined.AutoFixHigh)
        )
    )

    // "Get photos" section with import/share actions
    data object GetPhotos : BottomSheetSection(
        titleRes = R.string.photos,
        items = listOf(
            BottomSheetItem(7, R.string.share_with_partner, icon = Icons.Outlined.PersonAdd),
            BottomSheetItem(8, R.string.import_from_other_places, icon = Icons.Outlined.FileDownload)
        )
    )
}