package com.example.photoclone.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.photoclone.data.model.Photo

/**
 * Adaptive grid that chooses a sensible column count based on screen width.
 * Keeps gutters consistent and exposes a simple items callback so callers can
 * reuse existing item composables (e.g. `SelectablePhotoGridItem`).
 */
@Composable
fun AdaptivePhotoGrid(
    photos: List<Photo>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(2.dp),
    gutter: Dp = 2.dp,
    itemContent: @Composable (index: Int, photo: Photo) -> Unit
) {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp

    val columns = calculateColumnsForWidth(widthDp)

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(gutter),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(gutter)
    ) {
        itemsIndexed(photos) { index, photo ->
            itemContent(index, photo)
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
