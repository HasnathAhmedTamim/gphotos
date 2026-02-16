package com.example.photoclone.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.photoclone.R


/**
 * Full-screen photo pager with tap-to-toggle chrome and basic pinch/double-tap zoom.
 * - Single tap toggles chrome (back + action overlays).
 * - Pinch to zoom and pan when zoomed.
 * - Double-tap toggles between 1x and a chosen zoom (2x).
 * - Pager horizontal scrolling is disabled while the active page is zoomed (>1f).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPager(
    photoUrls: List<String>, // list of remote/local image URLs
    initialPage: Int, // starting page index
    onDismiss: () -> Unit, // called when back is pressed
    modifier: Modifier = Modifier
) {
    // Pager state holds current page and pageCount
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photoUrls.size }
    )

    // Track whether the currently-visible page is zoomed (scale > 1f)
    var pageScale by rememberSaveable { mutableStateOf(1f) }

    // Chrome visibility toggled by single tap on the image; saved across recompositions
    var chromeVisible by rememberSaveable { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        // HorizontalPager: disable user scroll when zoomed on the active page
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = pageScale <= 1.01f,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Per-page transformable state: scale and translation
            var localScale by remember { mutableStateOf(1f) }
            var targetScale by remember { mutableStateOf(1f) }
            val animatedScale by animateFloatAsState(targetValue = targetScale)

            var offset by remember { mutableStateOf(Offset.Zero) }

            val transformState = rememberTransformableState { zoomChange, pan, _ ->
                // Update local scale and offset directly (coarse, but fast)
                val newScale = (localScale * zoomChange).coerceIn(1f, 4f)
                localScale = newScale
                targetScale = newScale
                offset += pan
            }

            // If this page is active, propagate the scale to the parent-tracked pageScale
            LaunchedEffect(animatedScale, pagerState.currentPage) {
                if (page == pagerState.currentPage) {
                    pageScale = animatedScale
                }
            }

            // Reset translation when returning to 1x
            LaunchedEffect(targetScale) {
                if (targetScale <= 1f) {
                    offset = Offset.Zero
                }
            }

            // Image receives tap gestures to toggle chrome; double-tap toggles zoom
            PhotoImage(
                imageUrl = photoUrls[page],
                contentDescription = stringResource(R.string.photo_index_description, page + 1, photoUrls.size),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { chromeVisible = !chromeVisible },
                            onDoubleTap = {
                                // Double-tap toggles between 1x and 2x
                                val new = if (animatedScale > 1.5f) 1f else 2f
                                targetScale = new
                                localScale = new
                            }
                        )
                    }
                    .transformable(state = transformState)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        translationX = offset.x
                        translationY = offset.y
                    },
                contentScale = ContentScale.Fit,
                requestSizePx = null,
                showPlaceholder = true
            )
        }

        // Top-left back button overlay (animated)
        AnimatedVisibility(
            visible = chromeVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                IconButton(onClick = onDismiss, modifier = Modifier.padding(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Small bottom action row overlay (share/edit/delete) — animated with chrome
        AnimatedVisibility(
            visible = chromeVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO: share */ }) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* TODO: edit */ }) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* TODO: delete */ }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}
