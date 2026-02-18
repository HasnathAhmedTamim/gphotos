package com.example.photoclone.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dimensions for PhotoClone app
 * Optimized for your existing component structure
 */
object Dimens {

    // Photo Grid (used in AdaptivePhotoGrid, PhotoGridItem)
    object Grid {
        val spacing = 2.dp
        val spacingLarge = 4.dp
        val columns = 3
        val columnsLandscape = 5
    }

    // Navigation (BottomNavigation, TopAppBar)
    object Nav {
        val bottomHeight = 80.dp
        val topHeight = 64.dp
        val iconSize = 24.dp
        val elevation = 3.dp
    }

    // Selection (PhotoGridItem selection)
    object Selection {
        val checkboxSize = 24.dp
        val checkboxPadding = 8.dp
        val bottomSheetHeight = 160.dp
        val bottomSheetExpanded = 240.dp
        val actionButtonSize = 64.dp
        val actionIconSize = 28.dp
    }

    // Photo Viewer (PhotoPager component)
    object Viewer {
        val actionBarHeight = 72.dp
        val buttonSize = 48.dp
        val iconSize = 24.dp
        val indicatorSize = 6.dp
    }

    // Common spacing (use throughout app)
    object Spacing {
        val xs = 2.dp
        val small = 4.dp
        val medium = 8.dp
        val large = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
    }

    // Cards & Items
    object Item {
        val cornerRadius = 12.dp
        val elevation = 2.dp
        val minTouch = 48.dp
        val thumbnailSize = 56.dp
        val listHeight = 72.dp
    }

    // Create Screen
    object Create {
        val tileSize = 100.dp
        val iconSize = 36.dp
        val spacing = 12.dp
    }
}
