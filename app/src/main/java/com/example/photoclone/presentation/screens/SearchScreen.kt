package com.example.photoclone.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.R
import com.example.photoclone.presentation.components.PhotoBottomNavigation
import com.example.photoclone.presentation.components.BottomNavItem
import com.example.photoclone.presentation.components.SelectablePhotoGridItem
import com.example.photoclone.presentation.components.SelectionBottomSheet
import com.example.photoclone.presentation.viewmodel.PhotoSelectionViewModel
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    photos: List<String>,
    currentRoute: String,
    onNavigate: (String) -> Unit = {},
    viewModel: PhotoSelectionViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val photoList by viewModel.photos.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()

    // Initialize ViewModel with photos data (convert URLs to Photo objects)
    LaunchedEffect(photos) {
        val photoObjects = photos.mapIndexed { index, url ->
            com.example.photoclone.data.model.Photo(
                id = index.toLong(),
                imageUrl = url,
                thumbnailUrl = url
            )
        }
        viewModel.setPhotos(photoObjects)
    }

    // Filter photos based on search query
    val filteredPhotos = if (searchQuery.isEmpty()) {
        photoList
    } else {
        photoList.filter { it.imageUrl.contains(searchQuery, ignoreCase = true) }
    }

    // Build navigation items
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
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(R.string.search)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        )
                    )
                },
                navigationIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            PhotoBottomNavigation(
                items = navigationItems,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    onNavigate(navigationItems[index].route)
                }
            )
        }
    ) { paddingValues ->
        if (filteredPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isEmpty()) {
                        stringResource(R.string.no_photos)
                    } else {
                        "No photos found for \"$searchQuery\""
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Columns state controllable by pinch gesture
            var columns by remember { mutableStateOf(3) }
            val gridState = rememberLazyGridState()

            // Compute per-item pixel size for image requests
            val windowInfo = androidx.compose.ui.platform.LocalWindowInfo.current
            val density = androidx.compose.ui.platform.LocalDensity.current
            val widthDp = with(density) { windowInfo.containerSize.width.toDp().value }
            val gutterDp = 2.dp.value
            val totalGutterDp = (columns + 1) * gutterDp
            val availableDp = widthDp - totalGutterDp
            val itemDp = (availableDp / columns).coerceAtLeast(1f)
            val itemPx = with(density) { itemDp.dp.toPx().toInt() }

            // Determine when to show persistent bottom sheet (selection active + count > 0)
            val showBottomSheet by remember(selectedCount, isSelectionMode) {
                derivedStateOf { isSelectionMode && selectedCount > 0 }
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    state = gridState,
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 2.dp,
                        end = 2.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (isSelectionMode) Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset: Offset ->
                                    val info = gridState.layoutInfo
                                    val item = info.visibleItemsInfo.firstOrNull { vi ->
                                        val left = vi.offset.x.toFloat()
                                        val top = vi.offset.y.toFloat()
                                        val right = (vi.offset.x + vi.size.width).toFloat()
                                        val bottom = (vi.offset.y + vi.size.height).toFloat()
                                        offset.x >= left && offset.x <= right && offset.y >= top && offset.y <= bottom
                                    }
                                    item?.let { vi ->
                                        filteredPhotos.getOrNull(vi.index)?.let { photo ->
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
                                        filteredPhotos.getOrNull(vi.index)?.let { photo ->
                                            viewModel.setSelection(photo, true)
                                        }
                                    }
                                    change.consume()
                                }
                            )
                        } else Modifier)
                ) {
                    items(filteredPhotos) { photo ->
                        SelectablePhotoGridItem(
                            imageUrl = photo.imageUrl,
                            isSelectionMode = isSelectionMode,
                            isSelected = photo.isSelected,
                            onClick = {
                                if (isSelectionMode) viewModel.toggleSelection(photo)
                            },
                            onLongPress = {
                                if (!isSelectionMode) viewModel.startSelectionMode(photo)
                            },
                            imageRequestSizePx = itemPx
                        )
                    }
                }

                // Persistent bottom sheet overlay (non-modal) so grid remains usable
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
}
