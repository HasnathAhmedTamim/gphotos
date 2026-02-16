package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.photoclone.data.model.Photo

/**
 * Adaptive grid that picks a sensible column count from screen width.
 * Supports occasional larger items via [bigItemPredicate].
 */
@Composable
fun AdaptivePhotoGrid(
    photos: List<Photo>, // items to show
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(2.dp), // outer padding
    gutter: Dp = 2.dp, // space between items
    columns: Int? = null, // override column calculation when provided
    state: LazyGridState? = null, // optional state for scroll/gestures
    bigItemPredicate: (index: Int, photo: Photo) -> Boolean = { _, _ -> false }, // span selector
    itemContent: @Composable (index: Int, photo: Photo, imageRequestSizePx: Int?, itemModifier: Modifier) -> Unit
) {
    // Window width in dp (avoids Configuration.screenWidthDp lint)
    val windowInfo = LocalWindowInfo.current
    val widthDp = with(LocalDensity.current) { windowInfo.containerSize.width.toDp().value.toInt() }

    // Use provided columns or compute from width
    val computedColumns = columns ?: calculateColumnsForWidth(widthDp)

    // Ensure a non-null LazyGridState
    val gridState = state ?: rememberLazyGridState()

    // Compute item pixel size for image requests (subtract gutters/padding)
    val density = LocalDensity.current
    val totalGutterDp = (computedColumns + 1) * gutter.value
    val availableDp = widthDp - totalGutterDp
    val itemDp = (availableDp / computedColumns).coerceAtLeast(1f)
    val itemPx = with(density) { itemDp.dp.toPx().toInt() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(computedColumns),
        state = gridState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(gutter),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(gutter)
    ) {
        // Emit each photo item and optionally let some span multiple columns
        photos.forEachIndexed { index, photo ->
            val span = if (bigItemPredicate(index, photo)) computedColumns else 1
            item(key = photo.id, span = { GridItemSpan(span) }) {
                // Request a larger image for multi-column items
                val requestSize = if (span == 1) itemPx else itemPx * computedColumns
                // Pass a per-item modifier so callers can add animations
                itemContent(index, photo, requestSize, Modifier)
            }
        }
    }
}

internal fun calculateColumnsForWidth(widthDp: Int): Int {
    return when {
        widthDp < 480 -> 2
        widthDp < 720 -> 3
        widthDp < 960 -> 4
        else -> 5
    }
}
