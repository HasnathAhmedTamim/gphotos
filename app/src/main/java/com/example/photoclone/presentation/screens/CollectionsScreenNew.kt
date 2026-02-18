package com.example.photoclone.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.photoclone.presentation.model.AlbumItem
import com.example.photoclone.presentation.model.CategoryItem

/**
 * Google Photos-style Collections Screen
 *
 * Layout:
 * 1. Albums section (2-column grid)
 * 2. Categories section (list)
 *
 * All scrolls together in a single LazyColumn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreenNew(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CollectionsTopBar()
        },
        bottomBar = {
            GooglePhotos4TabBottomBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        CollectionsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Top App Bar for Collections screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionsTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Collections",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Main content - single scrollable LazyColumn with albums and categories
 */
@Composable
private fun CollectionsContent(
    modifier: Modifier = Modifier
) {
    // Sample albums data
    val albums = remember {
        listOf(
            AlbumItem(
                id = "1",
                title = "Camera",
                itemCount = 1234,
                thumbnailUrl = "https://picsum.photos/400/400?random=1"
            ),
            AlbumItem(
                id = "2",
                title = "Family Trip",
                itemCount = 245,
                thumbnailUrl = "https://picsum.photos/400/400?random=2"
            ),
            AlbumItem(
                id = "3",
                title = "Summer 2026",
                itemCount = 567,
                thumbnailUrl = "https://picsum.photos/400/400?random=3"
            ),
            AlbumItem(
                id = "4",
                title = "Vacation",
                itemCount = 189,
                thumbnailUrl = "https://picsum.photos/400/400?random=4"
            ),
            AlbumItem(
                id = "5",
                title = "Friends",
                itemCount = 423,
                thumbnailUrl = "https://picsum.photos/400/400?random=5"
            ),
            AlbumItem(
                id = "6",
                title = "Food",
                itemCount = 156,
                thumbnailUrl = "https://picsum.photos/400/400?random=6"
            )
        )
    }

    // Sample categories data
    val categories = remember {
        listOf(
            CategoryItem(
                id = "screenshots",
                name = "Screenshots",
                icon = Icons.Outlined.Screenshot
            ),
            CategoryItem(
                id = "videos",
                name = "Videos",
                icon = Icons.Outlined.VideoLibrary
            ),
            CategoryItem(
                id = "favorites",
                name = "Favorites",
                icon = Icons.Outlined.Favorite
            ),
            CategoryItem(
                id = "trash",
                name = "Trash",
                icon = Icons.Outlined.Delete
            ),
            CategoryItem(
                id = "archive",
                name = "Archive",
                icon = Icons.Outlined.Archive
            )
        )
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Albums Section Header
        item {
            Text(
                text = "Albums",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Albums Grid (chunked into rows of 2)
        items(albums.chunked(2)) { rowAlbums ->
            AlbumGridRow(
                albums = rowAlbums,
                onAlbumClick = { album ->
                    // TODO: Navigate to album details
                }
            )
        }

        // Space between sections
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Categories Section
        items(categories) { category ->
            CategoryRow(
                category = category,
                onClick = {
                    // TODO: Navigate to category
                }
            )

            // Divider between items (except last)
            if (category != categories.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

/**
 * Row containing 2 album cards
 */
@Composable
private fun AlbumGridRow(
    albums: List<AlbumItem>,
    onAlbumClick: (AlbumItem) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        albums.forEach { album ->
            AlbumCard(
                album = album,
                onClick = { onAlbumClick(album) },
                modifier = Modifier.weight(1f)
            )
        }

        // If only one album in row, add empty space
        if (albums.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Individual Album Card with square thumbnail and info
 */
@Composable
private fun AlbumCard(
    album: AlbumItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        // Square thumbnail image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (album.thumbnailUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(album.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = album.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder when no image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoAlbum,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Album title
        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Item count
        Text(
            text = "${album.itemCount} items",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Category row with icon and title
 */
@Composable
private fun CategoryRow(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Category name
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            // Chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Bottom Navigation Bar (4 tabs)
 */
@Composable
private fun GooglePhotos4TabBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "photos",
            onClick = { onNavigate("photos") },
            icon = {
                Icon(
                    if (currentRoute == "photos") Icons.Filled.Photo else Icons.Outlined.Photo,
                    "Photos"
                )
            },
            label = { Text("Photos") }
        )

        NavigationBarItem(
            selected = currentRoute == "collections",
            onClick = { onNavigate("collections") },
            icon = {
                Icon(
                    if (currentRoute == "collections") Icons.Filled.Collections else Icons.Outlined.Collections,
                    "Collections"
                )
            },
            label = { Text("Collections") }
        )

        NavigationBarItem(
            selected = currentRoute == "create",
            onClick = { onNavigate("create") },
            icon = {
                Icon(
                    if (currentRoute == "create") Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                    "Create"
                )
            },
            label = { Text("Create") }
        )

        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = { onNavigate("search") },
            icon = {
                Icon(
                    if (currentRoute == "search") Icons.Filled.Search else Icons.Outlined.Search,
                    "Search"
                )
            },
            label = { Text("Search") }
        )
    }
}
