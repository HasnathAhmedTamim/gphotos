package com.example.photoclone.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Google Photos Style Photo Viewer
 * - Clean, minimalist design
 * - Smooth animations
 * - Gesture controls (swipe, zoom, tap)
 * - Bottom sheet actions
 * - Info panel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GooglePhotosViewer(
    photos: List<String>,
    initialPage: Int = 0,
    onDismiss: () -> Unit,
    onDismissWithIndex: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photos.size }
    )

    var uiVisible by remember { mutableStateOf(true) }
    var showInfo by remember { mutableStateOf(false) }
    var showActions by remember { mutableStateOf(false) }

    // Track if the currently visible page is zoomed (scale > 1f)
    var isCurrentPageZoomed by remember { mutableStateOf(false) }

    // Auto-hide UI after 3 seconds
    LaunchedEffect(uiVisible) {
        if (uiVisible) {
            delay(3000)
            uiVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        BackHandler {
            // report current index before dismissing
            onDismissWithIndex(pagerState.currentPage)
            onDismiss()
        }

        // Photo Pager - disable swiping when the currently visible image is zoomed
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !isCurrentPageZoomed
        ) { page ->
            ZoomablePhotoView(
                imageUrl = photos[page],
                onTap = { uiVisible = !uiVisible },
                isActive = page == pagerState.currentPage,
                onZoomChanged = { zoomed ->
                    // only update when this page is active
                    if (page == pagerState.currentPage) {
                        isCurrentPageZoomed = zoomed
                    }
                },
                resetTrigger = pagerState.currentPage
            )
        }

        // Top Bar with smooth fade
        AnimatedVisibility(
            visible = uiVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            GooglePhotosTopBar(
                currentPage = pagerState.currentPage + 1,
                totalPages = photos.size,
                onBack = {
                    onDismissWithIndex(pagerState.currentPage)
                    onDismiss()
                },
                onInfo = { showInfo = true }
            )
        }

        // Bottom Action Bar
        AnimatedVisibility(
            visible = uiVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            GooglePhotosActionBar(
                onShare = { },
                onEdit = { },
                onFavorite = { },
                onDelete = { },
                onMore = { showActions = true }
            )
        }

        // Page Indicator
        if (photos.size > 1 && uiVisible) {
            PageIndicator(
                currentPage = pagerState.currentPage,
                totalPages = photos.size,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )
        }

        // Info Bottom Sheet
        if (showInfo) {
            PhotoInfoSheet(
                onDismiss = { showInfo = false }
            )
        }

        // Actions Bottom Sheet
        if (showActions) {
            PhotoActionsSheet(
                onDismiss = { showActions = false }
            )
        }
    }
}

@Composable
private fun GooglePhotosTopBar(
    currentPage: Int,
    totalPages: Int,
    onBack: () -> Unit,
    onInfo: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "$currentPage / $totalPages",
                color = Color.White,
                fontSize = 14.sp
            )

            IconButton(onClick = onInfo) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun GooglePhotosActionBar(
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onMore: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .padding(16.dp)
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(Icons.Filled.Share, "Share", onShare)
            ActionButton(Icons.Outlined.Edit, "Edit", onEdit)
            ActionButton(Icons.Outlined.FavoriteBorder, "Favorite", onFavorite)
            ActionButton(Icons.Outlined.Delete, "Delete", onDelete)
            ActionButton(Icons.Filled.MoreVert, "More", onMore)
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(minOf(totalPages, 5)) { index ->
            val alpha = when {
                index == currentPage -> 1f
                index == currentPage - 1 || index == currentPage + 1 -> 0.5f
                else -> 0.3f
            }

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun ZoomablePhotoView(
    imageUrl: String,
    onTap: () -> Unit,
    isActive: Boolean = true,
    onZoomChanged: (Boolean) -> Unit = {},
    resetTrigger: Int = 0
) {
    // Animated scale for smoother double-tap behavior
    val scaleAnim = remember { Animatable(1f) }
    val offsetXAnim = remember { Animatable(0f) }
    val offsetYAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val context = LocalContext.current

    // Track last reset so we only act when resetTrigger changes
    var lastReset by remember { mutableStateOf(resetTrigger) }
    LaunchedEffect(resetTrigger) {
        if (resetTrigger != lastReset) {
            lastReset = resetTrigger
            // animate back to identity
            coroutineScope.launch {
                scaleAnim.animateTo(1f, animationSpec = tween(durationMillis = 250))
                offsetXAnim.animateTo(0f, animationSpec = tween(durationMillis = 250))
                offsetYAnim.animateTo(0f, animationSpec = tween(durationMillis = 250))
            }
            onZoomChanged(false)
        }
    }

    // notify parent when zoom state changes (only when active page)
    LaunchedEffect(scaleAnim.value, isActive) {
        if (isActive) {
            onZoomChanged(scaleAnim.value > 1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(isActive) {
                detectTapGestures(
                    onTap = { if (isActive) onTap() },
                    onDoubleTap = { tapOffset ->
                        if (!isActive) return@detectTapGestures
                        coroutineScope.launch {
                            val target = if (scaleAnim.value > 1f) 1f else 2.5f
                            // animate scale
                            scaleAnim.animateTo(target, animationSpec = tween(durationMillis = 300))
                            // when zooming out, reset offsets
                            if (target == 1f) {
                                offsetXAnim.animateTo(0f, animationSpec = tween(300))
                                offsetYAnim.animateTo(0f, animationSpec = tween(300))
                            } else {
                                // try to center the tapped point roughly by adjusting offsets
                                // compute approximate max offsets based on container and scale
                                val maxOffsetX = (containerSize.width * (scaleAnim.value - 1f)) / 2f
                                val maxOffsetY = (containerSize.height * (scaleAnim.value - 1f)) / 2f
                                // translate tapped point towards center
                                val dx = (containerSize.width / 2f - tapOffset.x).coerceIn(-maxOffsetX, maxOffsetX)
                                val dy = (containerSize.height / 2f - tapOffset.y).coerceIn(-maxOffsetY, maxOffsetY)
                                offsetXAnim.animateTo(dx, animationSpec = tween(300))
                                offsetYAnim.animateTo(dy, animationSpec = tween(300))
                            }
                        }
                    }
                )
            }
            .pointerInput(isActive, scaleAnim.value > 1f) {
                // Only intercept transform gestures when zoomed
                // When not zoomed, let the HorizontalPager handle swipe gestures
                if (scaleAnim.value > 1f) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (!isActive) return@detectTransformGestures
                        coroutineScope.launch {
                            val newScale = (scaleAnim.value * zoom).coerceIn(1f, 5f)
                            scaleAnim.snapTo(newScale)

                            if (scaleAnim.value > 1f) {
                                // update offsets and clamp
                                val maxX = (containerSize.width * (scaleAnim.value - 1f)) / 2f
                                val maxY = (containerSize.height * (scaleAnim.value - 1f)) / 2f
                                val nextX = (offsetXAnim.value + pan.x).coerceIn(-maxX, maxX)
                                val nextY = (offsetYAnim.value + pan.y).coerceIn(-maxY, maxY)
                                offsetXAnim.snapTo(nextX)
                                offsetYAnim.snapTo(nextY)
                            } else {
                                offsetXAnim.snapTo(0f)
                                offsetYAnim.snapTo(0f)
                            }
                        }
                    }
                }
                // When not zoomed, don't add any gesture detection here
                // This allows swipe gestures to pass through to HorizontalPager
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val s = scaleAnim.value
                    scaleX = s
                    scaleY = s
                    translationX = offsetXAnim.value
                    translationY = offsetYAnim.value
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoInfoSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Photo Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow("Date", "Feb 17, 2026")
            InfoRow("Size", "2.4 MB")
            InfoRow("Dimensions", "1920 × 1080")
            InfoRow("Device", "Pixel 8 Pro")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoActionsSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ActionItem(Icons.Outlined.Download, "Download")
            ActionItem(Icons.Outlined.DriveFileMove, "Move to folder")
            ActionItem(Icons.Outlined.ContentCopy, "Make a copy")
            ActionItem(Icons.Outlined.Print, "Print")
            ActionItem(Icons.Outlined.Album, "Add to album")
            ActionItem(Icons.Outlined.Archive, "Archive")
            ActionItem(Icons.Outlined.Delete, "Move to trash")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ActionItem(icon: ImageVector, label: String) {
    Surface(
        onClick = { },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
