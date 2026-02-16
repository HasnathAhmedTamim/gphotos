package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.paging.compose.LazyPagingItems
import com.example.photoclone.data.model.Photo

@Composable
fun AdaptivePagingGrid(
    items: LazyPagingItems<Photo>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(2.dp),
    gutter: Dp = 2.dp,
    columns: Int? = null,
    state: LazyGridState? = null,
    // Allow callers to mark some items as "big" so they span multiple columns like Google Photos
    bigItemPredicate: (index: Int, photo: Photo?) -> Boolean = { _, _ -> false },
    itemContent: @Composable (index: Int, photo: Photo?, imageRequestSizePx: Int?, itemModifier: Modifier) -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val widthDp = with(LocalDensity.current) { windowInfo.containerSize.width.toDp().value.toInt() }
    val computedColumns = columns ?: calculateColumnsForWidth(widthDp)
    val gridState = state ?: rememberLazyGridState()
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
        horizontalArrangement = Arrangement.spacedBy(gutter),
        verticalArrangement = Arrangement.spacedBy(gutter)
    ) {
        val count = items.itemCount
        // Emit each item with explicit key and optional span so LazyVerticalGrid can preserve identity and create "big" tiles
        for (index in 0 until count) {
            val photo = items[index]
            val span = if (bigItemPredicate(index, photo)) computedColumns else 1
            // Use the stable index as the key for paging items. Using photo.id when the
            // item is initially null (placeholder) and later becomes non-null would change the key
            // and cause list instability and jumpy scroll behavior. Index is stable across load states.
            item(key = index, span = { GridItemSpan(span) }) {
                val requestSize = if (span == 1) itemPx else itemPx * computedColumns
                itemContent(index, photo, requestSize, Modifier)
            }
        }
    }
}
