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
/**
 *  Home screen showing the photo grid with selection and full-screen viewer.
 *  The screen is structured with a top app bar, a grid of photos, and a bottom navigation bar. It also includes a selection mode with a persistent bottom sheet for actions.
 *  - The top app bar includes the app logo and action icons for adding content, viewing notifications, and accessing the user profile. When in selection mode, it changes to show the number of selected items and a clear selection button.
 *  - The main content area displays a grid of photos, which can be tapped to open a full-screen pager. The grid supports pinch-to-resize to adjust the number of columns, and long-press to enter selection mode. When in selection mode, users can drag across photos to select multiple items.
 *  - The bottom navigation bar allows users to switch between different sections of the app (Photos, Collection, Create, Search), but it is hidden when in selection mode to focus on the selected items and available actions.
 *  - When photos are selected, a persistent bottom sheet appears with actions such as Share, Add to album, Create, Delete, Backup, Archive, Edit location, and Move to locked folder. Each action triggers a callback that can be implemented in the ViewModel to perform the corresponding operation on the selected photos.
 *  The UI is designed to closely mimic the look and feel of the Google Photos app, with attention to theming, typography, and layout. The use of a ViewModel allows for proper state management of the photo list and selection state, ensuring a responsive and interactive user experience. The screen is responsive and adapts to different screen sizes, ensuring a consistent user experience across devices. The photo grid is interactive, allowing users to easily view and manage their photos with familiar gestures and actions.
 *
 * */
// Home screen: shows photo grid, supports selection, pinch-to-resize, and full-screen pager.
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
    // Sync incoming URL list into the ViewModel as Photo objects (runs when photos change)
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

    // Observe ViewModel state (photos and selection state)
    val photoObjects by viewModel.photos.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

    // Derived state: show selection bottom sheet only when in selection mode and items selected
    val showBottomSheet by remember(selectedCount, isSelectionMode) {
        derivedStateOf { isSelectionMode && selectedCount > 0 }
    }

    // Pager controls for full-screen image viewer
    var showPager by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

    // Columns count adjustable via pinch gestures
    var columns by remember { mutableStateOf(3) }

    // Lazy grid state used for mapping pointer positions to visible items (drag-to-select)
    val gridState = rememberLazyGridState()

    // Bottom navigation items for the app
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
            // Show selection top bar when in selection mode, otherwise app top bar
            if (showPager) {
                // No top bar while full-screen pager is visible (pager provides its own back overlay)
                null
            } else if (isSelectionMode) {
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
                        // Optional actions menu while selecting
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
            // Hide bottom navigation during selection mode
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
            when {
                // Full-screen pager shown when a photo is opened
                showPager -> {
                    PhotoPager(
                        photoUrls = photos,
                        initialPage = selectedPhotoIndex,
                        onDismiss = { showPager = false }
                    )
                }

                // Loading and empty states
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                photos.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_photos),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    // Adaptive grid with pinch-to-resize and (optional) drag-to-select when in selection mode
                    AdaptivePhotoGrid(
                        photos = photoObjects,
                        modifier = Modifier
                            .fillMaxSize()
                            // Detect pinch gestures to increase/decrease columns
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
                            // If in selection mode, enable drag gestures to select multiple items
                            .then(if (isSelectionMode) Modifier.pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset: Offset ->
                                        // On drag start, select the item under the finger (if visible)
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
                                        // During drag, select any visible item under the current pointer position
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
                        // Each grid cell is a selectable tile that supports tap/long-press
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
            }

            // Persistent selection bottom sheet shown when items are selected
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
