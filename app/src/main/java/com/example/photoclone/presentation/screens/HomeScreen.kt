package com.example.photoclone.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.R
import com.example.photoclone.data.model.Photo
import com.example.photoclone.presentation.components.*
import com.example.photoclone.presentation.viewmodel.PhotoSelectionViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    photos: List<String>,
    currentRoute: String,
    isLoading: Boolean = false,
    onAddClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: PhotoSelectionViewModel = viewModel()
) {
    // Convert photo URLs to Photo objects
    LaunchedEffect(photos) {
        val photoObjects = photos.mapIndexed { index, url ->
            Photo(
                id = index.toLong(),
                imageUrl = url,
                thumbnailUrl = url
            )
        }
        viewModel.setPhotos(photoObjects)
    }

    val photoObjects by viewModel.photos.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    // Show bottom sheet when selection mode is active and at least one item is selected
    val showBottomSheet by remember(selectedCount, isSelectionMode) {
        derivedStateOf { isSelectionMode && selectedCount > 0 }
    }
    var showPager by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

    // Columns state controllable by pinch gesture
    var columns by remember { mutableStateOf(3) }

    // Grid state for mapping pointer positions to items for drag-to-select
    val gridState = rememberLazyGridState()

    val navigationItems = listOf(
        BottomNavItem(
            title = stringResource(R.string.photos),
            icon = Icons.Outlined.PhotoLibrary,
            route = "home"
        ),
        BottomNavItem(
            title = stringResource(R.string.collection),
            icon = Icons.Outlined.Collections,
            route = "collection"
        ),
        BottomNavItem(
            title = stringResource(R.string.create),
            icon = Icons.Outlined.AddCircle,
            route = "create"
        ),
        BottomNavItem(
            title = stringResource(R.string.search),
            icon = Icons.Outlined.Search,
            route = "search"
        )
    )

    val selectedIndex = navigationItems.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = {
                        Text("$selectedCount selected")
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, "Clear selection")
                        }
                    },
                    actions = {
                        // Optional manual actions button; bottom sheet visibility is driven by selection state
                        IconButton(onClick = { /* manual actions */ }) {
                            Icon(Icons.Default.MoreVert, "Actions")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            } else {
                PhotoTopAppBar(
                    onAddClick = onAddClick,
                    onNotificationClick = onNotificationClick,
                    onProfileClick = onProfileClick
                )
            }
        },
        bottomBar = {
            if (!isSelectionMode) {
                PhotoBottomNavigation(
                    items = navigationItems,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        onNavigate(navigationItems[index].route)
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showPager) {
                PhotoPager(
                    photoUrls = photos,
                    initialPage = selectedPhotoIndex,
                    onDismiss = { showPager = false }
                )
            } else if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (photos.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_photos),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AdaptivePhotoGrid(
                    photos = photoObjects,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                if (zoom > 1f) {
                                    // pinch out -> fewer columns (bigger thumbnails)
                                    columns = (columns - 1).coerceAtLeast(2)
                                } else if (zoom < 1f) {
                                    // pinch in -> more columns (smaller thumbnails)
                                    columns = (columns + 1).coerceAtMost(6)
                                }
                            }
                        }
                        .then(if (isSelectionMode) Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset: Offset ->
                                    // select initial item under finger
                                    val info = gridState.layoutInfo
                                    val item = info.visibleItemsInfo.firstOrNull { vi ->
                                        val left = vi.offset.x.toFloat()
                                        val top = vi.offset.y.toFloat()
                                        val right = (vi.offset.x + vi.size.width).toFloat()
                                        val bottom = (vi.offset.y + vi.size.height).toFloat()
                                        offset.x >= left && offset.x <= right && offset.y >= top && offset.y <= bottom
                                    }
                                    item?.let { vi ->
                                        photoObjects.getOrNull(vi.index)?.let { photo ->
                                            viewModel.setSelection(photo, true)
                                        }
                                    }
                                },
                                onDrag = { change, _ ->
                                    val pos = change.position
                                    val info = gridState.layoutInfo
                                    val item = info.visibleItemsInfo.firstOrNull { vi ->
                                        val left = vi.offset.x.toFloat()
                                        val top = vi.offset.y.toFloat()
                                        val right = (vi.offset.x + vi.size.width).toFloat()
                                        val bottom = (vi.offset.y + vi.size.height).toFloat()
                                        pos.x >= left && pos.x <= right && pos.y >= top && pos.y <= bottom
                                    }
                                    item?.let { vi ->
                                        photoObjects.getOrNull(vi.index)?.let { photo ->
                                            viewModel.setSelection(photo, true)
                                        }
                                    }
                                    change.consume()
                                }
                            )
                        } else Modifier),
                    contentPadding = PaddingValues(4.dp),
                    gutter = 4.dp,
                    columns = columns,
                    state = gridState,
                    bigItemPredicate = { index, _ -> index % 10 == 0 }
                ) { index, photo, imageRequestSizePx, itemModifier ->
                    SelectablePhotoGridItem(
                        imageUrl = photo.imageUrl,
                        isSelected = photo.isSelected,
                        isSelectionMode = isSelectionMode,
                        onClick = {
                            if (isSelectionMode) {
                                viewModel.toggleSelection(photo)
                            } else {
                                selectedPhotoIndex = index
                                showPager = true
                            }
                        },
                        onLongPress = {
                            if (!isSelectionMode) {
                                viewModel.startSelectionMode(photo)
                            }
                        },
                        modifier = itemModifier,
                        imageRequestSizePx = imageRequestSizePx
                    )
                }
            }

            // Persistent bottom sheet: replace modal bottom sheet with non-modal Surface so grid remains interactive
            if (showBottomSheet) {
                SelectionBottomSheet(
                    selectedCount = selectedCount,
                    onClear = { viewModel.clearSelection() },
                    onShare = { viewModel.shareSelected() },
                    onAddToAlbum = { viewModel.addToAlbum() },
                    onCreate = { /* create */ },
                    onDelete = { viewModel.deleteSelected() },
                    onBackup = { viewModel.backupSelected() },
                    onArchive = { viewModel.archiveSelected() },
                    onMoveToLocked = { viewModel.moveToLockedFolder() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                )
            }
        }
    }
}
