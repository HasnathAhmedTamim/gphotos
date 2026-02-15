package com.example.photoclone.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.R
import com.example.photoclone.data.model.Photo
import com.example.photoclone.presentation.components.*
import com.example.photoclone.presentation.viewmodel.PhotoSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPager by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    gutter = 4.dp
                ) { index, photo ->
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
                        }
                    )
                }
            }
        }

        // Show bottom sheet when in selection mode
        LaunchedEffect(isSelectionMode, selectedCount) {
            showBottomSheet = isSelectionMode && selectedCount > 0
        }

        if (showBottomSheet) {
            PhotoActionsBottomSheet(
                selectedCount = selectedCount,
                onShare = { viewModel.shareSelected() },
                onAddToAlbum = { viewModel.addToAlbum() },
                onCreate = { /* TODO: Navigate to create */ },
                onDelete = { viewModel.deleteSelected() },
                onBackup = { viewModel.backupSelected() },
                onArchive = { viewModel.archiveSelected() },
                onDeleteFromArchive = { viewModel.deleteSelected() },
                onEditLocation = { /* TODO */ },
                onMoveToLocked = { viewModel.moveToLockedFolder() },
                onDismiss = { viewModel.clearSelection() }
            )
        }
    }
}

