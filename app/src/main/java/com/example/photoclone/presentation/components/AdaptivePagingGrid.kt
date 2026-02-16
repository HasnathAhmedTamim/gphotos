package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
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
        items(count) { index ->
            val photo = items[index]
            itemContent(index, photo, itemPx, Modifier)
        }
    }
}
