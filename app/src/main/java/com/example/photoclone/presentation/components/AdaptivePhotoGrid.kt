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
 * Adaptive grid that chooses a sensible column count based on screen width.
 * Supports occasional larger-spanning items via [bigItemPredicate].
 * If `columns` is provided, it will be used instead of calculating from width
 * Accepts an optional [state] so callers can read layout info for gestures (e.g., drag-to-select).
 */
@Composable
fun AdaptivePhotoGrid(
    photos: List<Photo>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(2.dp),
    gutter: Dp = 2.dp,
    columns: Int? = null,
    state: LazyGridState? = null,
    bigItemPredicate: (index: Int, photo: Photo) -> Boolean = { _, _ -> false },
    itemContent: @Composable (index: Int, photo: Photo, imageRequestSizePx: Int?, itemModifier: Modifier) -> Unit
) {
    // Use the window container size (pixels) converted to dp to determine columns; this avoids the
    // lint warning about Configuration.screenWidthDp.
    val windowInfo = LocalWindowInfo.current
    val widthDp = with(LocalDensity.current) { windowInfo.containerSize.width.toDp().value.toInt() }

    val computedColumns = columns ?: calculateColumnsForWidth(widthDp)

    // Ensure we always have a non-null state to pass to LazyVerticalGrid
    val gridState = state ?: rememberLazyGridState()

    // Compute per-item pixel size to request from the image loader. We subtract gutters/padding.
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
        // Use explicit items via the LazyGridScope.item { } so we can control spans per-item.
        photos.forEachIndexed { index, photo ->
            val span = if (bigItemPredicate(index, photo)) computedColumns else 1
            item(span = { GridItemSpan(span) }) {
                // Provide a per-item modifier so callers can add item animations or other modifiers.
                val requestSize = if (span == 1) itemPx else itemPx * computedColumns
                itemContent(index, photo, requestSize, Modifier.animateItem())
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
