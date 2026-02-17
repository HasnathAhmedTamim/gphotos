package com.example.photoclone.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.photoclone.R

/**
 * Metadata for a single photo (Google Photos style)
 */
data class PhotoMetadata(
    val date: String? = null,
    val location: String? = null,
    val deviceInfo: String? = null,
    val fileSize: String? = null,
    val resolution: String? = null
)

/**
 * Full-screen photo pager matching Google Photos UX (2026).
 *
 * Features:
 * - Single tap toggles chrome (top + bottom bars)
 * - Pinch to zoom and pan when zoomed
 * - Double-tap toggles zoom (1x ↔ 2x)
 * - Swipe left/right to navigate photos
 * - Top bar: Back, Date/Info, More menu
 * - Bottom bar: Favorite, Edit, Share, Add to Album, Delete
 * - Info panel (swipe up bottom sheet)
 *
 * @param photoUrls List of image URLs
 * @param initialPage Starting page index
 * @param onDismiss Called when back is pressed
 * @param onRequestPage Optional dynamic page loading callback
 * @param photoMetadata Optional metadata for current photo (date, location, etc.)
 * @param isFavorite Current photo favorite state
 * @param onFavoriteToggle Toggle favorite for current photo
 * @param onEdit Open edit screen for current photo
 * @param onShare Share current photo
 * @param onAddToAlbum Add current photo to album
 * @param onDelete Delete current photo
 * @param onMoreOptions Open more options menu
 * @param onInfoClick Open info panel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPager(
    photoUrls: List<String?>,
    initialPage: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onRequestPage: ((Int) -> String?)? = null,
    photoMetadata: ((Int) -> PhotoMetadata?)? = null,
    isFavorite: ((Int) -> Boolean)? = null,
    onFavoriteToggle: ((Int) -> Unit)? = null,
    onEdit: ((Int) -> Unit)? = null,
    onShare: ((Int) -> Unit)? = null,
    onAddToAlbum: ((Int) -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    onMoreOptions: ((Int) -> Unit)? = null,
    onInfoClick: ((Int) -> Unit)? = null
) {
    // Pager state holds current page and pageCount
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photoUrls.size }
    )

    // Map to keep per-page transform state so zoom/offset are preserved while swiping
    val perPageState = remember { mutableStateMapOf<Int, TransformState>() }

    // Dynamic page loading: maintain a cache of loaded page URLs when onRequestPage is provided
    val loadedPages = remember { mutableStateMapOf<Int, String?>() }

    // Initialize loaded pages from photoUrls (only when onRequestPage is provided)
    // For mock/in-memory mode, we access photoUrls directly so no need to populate cache
    LaunchedEffect(photoUrls, onRequestPage) {
        if (onRequestPage != null) {
            photoUrls.forEachIndexed { index, url ->
                if (url != null) loadedPages[index] = url
            }
        }
    }

    // Chrome visibility toggled by single tap on the image; saved across recompositions
    var chromeVisible by rememberSaveable { mutableStateOf(true) }

    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        // Intercept system back to dismiss the pager instead of closing the app
        BackHandler(enabled = true) {
            onDismiss()
        }

        // Prefetch neighbors when current page changes
        LaunchedEffect(pagerState.currentPage) {
            val current = pagerState.currentPage
            listOf(current - 1, current, current + 1).forEach { idx ->
                if (idx in 0 until photoUrls.size) {
                    // Try to load from onRequestPage if available and not already cached
                    if (onRequestPage != null && !loadedPages.containsKey(idx)) {
                        val url = onRequestPage(idx)
                        if (url != null) {
                            loadedPages[idx] = url
                            prefetchImage(context, url)
                        }
                    } else {
                        // Use existing URL from photoUrls or loadedPages
                        val url = loadedPages[idx] ?: photoUrls.getOrNull(idx)
                        url?.let { prefetchImage(context, it) }
                    }
                }
            }
        }

        // HorizontalPager: disable user scrolling when the active page is zoomed (>1f).
        // Compute whether the pager should allow scrolling based on the active page's scale.
        val currentPageScale by remember {
            derivedStateOf { perPageState[pagerState.currentPage]?.scale ?: 1f }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = currentPageScale <= 1f,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Ensure a TransformState exists for this page
            val state = perPageState.getOrPut(page) { TransformState(scale = 1f, offset = Offset.Zero) }
            val animatedScale by animateFloatAsState(targetValue = state.scale)

            // If this page is active, and scaled > 1, we want to disable pager swipe
            val isActive = page == pagerState.currentPage

            // Pointer handlers for taps/double-tap, multi-touch transforms (pinch) and panning when zoomed.
            // KEY: Only add tap gestures when we need them (zoomed), otherwise let pager handle ALL touches
            Box(
                Modifier
                    .fillMaxSize()
                    // Only add tap detection if zoomed OR if we're the active page
                    .then(
                        if (isActive) {
                            Modifier.pointerInput(page) {
                                detectTapGestures(
                                    onTap = {
                                        chromeVisible = !chromeVisible
                                    },
                                    onDoubleTap = { tapPos: Offset ->
                                        // toggle between 1x and 2x zoom
                                        val containerSize = size
                                        val target = if (state.scale > 1.5f) 1f else 2f
                                        val containerCenter = Offset(containerSize.width / 2f, containerSize.height / 2f)
                                        val focusToCenter = tapPos - containerCenter
                                        val scaleRatio = target / state.scale
                                        val newOffset = (state.offset + focusToCenter) * scaleRatio - focusToCenter
                                        state.scale = target
                                        state.offset = newOffset.clampOffset()
                                    }
                                )
                            }
                        } else Modifier
                    )
                    // multi-touch pinch/rotate -> detectTransformGestures (doesn't run for single-finger swipes)
                    .pointerInput(page) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val prevScale = state.scale
                            val newScale = (prevScale * zoom).coerceIn(1f, 4f)
                            state.scale = newScale
                            if (newScale > 1f) {
                                state.offset += pan
                                state.offset = state.offset.clampOffset()
                            } else if (prevScale > 1f && newScale <= 1f) {
                                state.offset = Offset.Zero
                            }
                        }
                    }
                    // single-finger drag for panning when zoomed: attach this pointer handler
                    // only when the page is zoomed (>1f) so it doesn't intercept horizontal
                    // swipes when at normal scale.
                    .then(if (state.scale > 1f) Modifier.pointerInput(page) {
                        detectDragGestures { change, dragAmount ->
                            state.offset += Offset(dragAmount.x, dragAmount.y)
                            state.offset = state.offset.clampOffset()
                            change.consume()
                        }
                    } else Modifier)
                     .graphicsLayer {
                         scaleX = animatedScale
                         scaleY = animatedScale
                         translationX = state.offset.x
                         translationY = state.offset.y
                     }
                 ) {
                if (photoUrls.isNotEmpty()) {
                    // When onRequestPage is provided (dynamic loading), use loadedPages cache
                    // Otherwise (mock/in-memory mode), directly use photoUrls for immediate access
                    val url = if (onRequestPage != null) {
                        loadedPages[page] ?: photoUrls.getOrNull(page)
                    } else {
                        photoUrls.getOrNull(page)
                    }

                    // Full-screen viewer: always use ORIGINAL size + Fit for maximum clarity
                    PhotoImage(
                        imageUrl = url,
                        contentDescription = stringResource(R.string.photo_index_description, page + 1, photoUrls.size),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,  // Fit maintains aspect ratio, no cropping
                        useOriginalSize = true,  // Load full resolution, not thumbnail
                        showPlaceholder = true
                    )
                }
            }

            // When this page becomes inactive and scale returns to 1, reset offset to zero
            LaunchedEffect(isActive, state.scale) {
                if (!isActive && state.scale <= 1f) {
                    // small delay to allow animation to settle
                    state.offset = Offset.Zero
                }
            }
        }

        // Top bar overlay (Google Photos style) - animated with chrome
        AnimatedVisibility(
            visible = chromeVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Center: Date/Location info (clickable to open info panel)
                    val metadata = photoMetadata?.invoke(pagerState.currentPage)
                    if (metadata != null) {
                        TextButton(
                            onClick = { onInfoClick?.invoke(pagerState.currentPage) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                metadata.date?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                metadata.location?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // More options menu
                    IconButton(onClick = { onMoreOptions?.invoke(pagerState.currentPage) }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Bottom action bar (Google Photos style) — animated with chrome
        AnimatedVisibility(
            visible = chromeVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentPage = pagerState.currentPage
                    val photoIsFavorite = isFavorite?.invoke(currentPage) ?: false

                    // Favorite button (heart icon)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onFavoriteToggle?.invoke(currentPage) }) {
                            Icon(
                                imageVector = if (photoIsFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (photoIsFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (photoIsFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Edit button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onEdit?.invoke(currentPage) }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Share button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onShare?.invoke(currentPage) }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Add to album button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onAddToAlbum?.invoke(currentPage) }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add to album",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Delete button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onDelete?.invoke(currentPage) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// Simple holder for transform state per page
private data class TransformState(var scale: Float, var offset: Offset)

// Clamp offset to some reasonable bounds. For precise bounds we'd measure image vs container; this uses a conservative clamp.
private fun Offset.clampOffset(maxPx: Float = 2000f): Offset {
    return Offset(x.coerceIn(-maxPx, maxPx), y.coerceIn(-maxPx, maxPx))
}
