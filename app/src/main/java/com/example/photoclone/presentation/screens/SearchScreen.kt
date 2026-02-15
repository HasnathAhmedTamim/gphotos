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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.R
import com.example.photoclone.presentation.components.PhotoBottomNavigation
import com.example.photoclone.presentation.components.BottomNavItem
import com.example.photoclone.presentation.components.SelectablePhotoGridItem
import com.example.photoclone.presentation.viewmodel.PhotoSelectionViewModel

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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 2.dp,
                    end = 2.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
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
        }
    }
}
