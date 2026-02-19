@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNUSED_VARIABLE", "UNUSED_VALUE")

package com.example.photoclone.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photoclone.R
import com.example.photoclone.presentation.components.CreateNewBottomSheet
import com.example.photoclone.presentation.components.GooglePhotosGrid
import com.example.photoclone.presentation.components.GooglePhotosViewer
import com.example.photoclone.presentation.model.CreateAction
import com.example.photoclone.presentation.model.CreateSection

/**
 * Google Photos Style Home Screen
 * - Clean Material 3 design
 * - Search bar
 * - Smart suggestions
 * - Smooth transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GooglePhotosHomeScreen(
    photos: List<String>,
    currentRoute: String,
    appliedFilter: String? = null,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showViewer by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showCreateSheet by remember { mutableStateOf(false) }
    // Quick filters: list + selected state (hoisted)
    // Only these three are actionable per request
    val quickFilters = listOf("Recent", "Favorites", "Videos")
    val snackbarHostState = remember { SnackbarHostState() }
    var activeFilter by remember { mutableStateOf(appliedFilter) }
    LaunchedEffect(appliedFilter) {
        activeFilter = appliedFilter
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Keep original top app bar (not the in-content search bar)
            if (!showViewer) {
                GooglePhotosTopAppBar(
                    searchQuery = searchQuery,
                    showSearch = showSearch,
                    isSelectionMode = isSelectionMode,
                    onSearchQueryChange = { searchQuery = it },
                    onProfileClick = { onNavigate("profile") },
                    onAddClick = { showCreateSheet = true },
                    // indicate that an in-content search bar is used to avoid duplicating actions
                    useInContentSearch = false,
                    onNotificationsClick = { onNavigate("notifications") }
                )
            }
        },
        bottomBar = {
            // Hide bottom navigation during selection mode or when viewing photo
            AnimatedVisibility(
                visible = !isSelectionMode && !showViewer,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                GooglePhotosBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = onNavigate
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = modifier.padding(padding)) {
            if (showViewer) {
                GooglePhotosViewer(
                    photos = photos,
                    initialPage = selectedPhotoIndex,
                    onDismiss = { showViewer = false },
                    onDismissWithIndex = { idx -> selectedPhotoIndex = idx }
                )
            } else {
                Column {
                    // Collapsible suggestions chip row
                    CollapsibleSuggestionsRow(
                        filters = quickFilters,
                        onFilterClick = { filter ->
                            // Map filter to route and navigate
                            when (filter) {
                                "Recent" -> onNavigate("filter/recent")
                                "Favorites" -> onNavigate("filter/favorites")
                                "Videos" -> onNavigate("filter/videos")
                                else -> {}
                            }
                        }
                    )

                    // If a filter was applied via the detail screen, use it to compute displayPhotos
                    val displayPhotos = remember(photos, activeFilter) {
                        if (activeFilter.isNullOrEmpty()) photos else {
                            when (activeFilter?.lowercase()) {
                                "recent" -> photos.takeLast(12.coerceAtMost(photos.size))
                                "favorites" -> photos.filterIndexed { idx, _ -> idx % 5 == 0 }
                                "videos" -> photos.filterIndexed { idx, _ -> idx % 3 == 0 }
                                else -> photos
                            }
                        }
                    }

                    // Photo grid
                    GooglePhotosGrid(
                        photos = displayPhotos,
                        onPhotoClick = { index ->
                            // Map clicked photo back to original photos index for viewer
                            val originalIndex = photos.indexOf(displayPhotos[index]).takeIf { it >= 0 } ?: index
                            selectedPhotoIndex = originalIndex
                            showViewer = true
                        },
                        onSelectionModeChange = { inSelectionMode ->
                            isSelectionMode = inSelectionMode
                        }
                    )

                    // Show a snackbar when a filter is applied offering to clear it
                    LaunchedEffect(activeFilter) {
                        if (!activeFilter.isNullOrEmpty()) {
                            val res = snackbarHostState.showSnackbar("Filter applied: ${activeFilter}", actionLabel = "Clear")
                            if (res == SnackbarResult.ActionPerformed) {
                                activeFilter = null
                            }
                        }
                    }
                }
            }

            // Create New Bottom Sheet (independent of selection mode)
            // Placed inside Box but after content so it overlays everything
            if (showCreateSheet) {
                CreateNewBottomSheet(
                    onDismiss = { showCreateSheet = false },
                    sections = getCreateSections()
                )
            }
        }
        // Snackbar host
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

/**
 * Get the sections and actions for the Create New bottom sheet
 * Exactly matches Google Photos layout
 */
@Composable
private fun getCreateSections(): List<CreateSection> {
    return listOf(
        // Primary actions (no title)
        CreateSection(
            title = null,
            actions = listOf(
                CreateAction(
                    id = "album",
                    title = "Album",
                    icon = Icons.Outlined.PhotoAlbum,
                    onClick = { /* TODO: Create album */ }
                ),
                CreateAction(
                    id = "collage",
                    title = "Collage",
                    icon = Icons.Outlined.ViewModule,
                    onClick = { /* TODO: Create collage */ }
                ),
                CreateAction(
                    id = "highlight_video",
                    title = "Highlight video",
                    icon = Icons.Outlined.Movie,
                    hasNewBadge = true,
                    onClick = { /* TODO: Create highlight video */ }
                ),
                CreateAction(
                    id = "cinematic_photo",
                    title = "Cinematic photo",
                    icon = Icons.Outlined.CameraAlt,
                    onClick = { /* TODO: Create cinematic photo */ }
                ),
                CreateAction(
                    id = "animation",
                    title = "Animation",
                    icon = Icons.Outlined.Animation,
                    onClick = { /* TODO: Create animation */ }
                ),
                CreateAction(
                    id = "remix",
                    title = "Remix",
                    icon = Icons.Outlined.Shuffle,
                    onClick = { /* TODO: Create remix */ }
                )
            )
        ),
        // Secondary actions (with "Get photos" title section)
        CreateSection(
            title = null,
            actions = listOf(
                CreateAction(
                    id = "get_photos",
                    title = "Get photos",
                    icon = Icons.Outlined.Download,
                    onClick = { /* TODO: Get photos */ }
                ),
                CreateAction(
                    id = "share_partner",
                    title = "Share with a partner",
                    icon = Icons.Outlined.PersonAdd,
                    onClick = { /* TODO: Share with partner */ }
                ),
                CreateAction(
                    id = "import",
                    title = "Import from other places",
                    icon = Icons.Outlined.CloudUpload,
                    onClick = { /* TODO: Import */ }
                )
            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsibleSuggestionsRow(
    filters: List<String>,
    onFilterClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Quick filters",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            SuggestionsChips(
                filters = filters,
                onFilterClick = onFilterClick
            )
        }

        HorizontalDivider(thickness = 1.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestionsChips(
    filters: List<String>,
    onFilterClick: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = false,
                    onClick = { onFilterClick(filter) },
                    leadingIcon = {
                        when (filter) {
                            "Recent" -> Icon(Icons.Filled.AccessTime, contentDescription = null, Modifier.size(18.dp))
                            "Favorites" -> Icon(Icons.Filled.Favorite, contentDescription = null, Modifier.size(18.dp))
                            "Videos" -> Icon(Icons.Filled.VideoLibrary, contentDescription = null, Modifier.size(18.dp))
                            else -> Icon(Icons.Filled.FilterList, contentDescription = null, Modifier.size(18.dp))
                        }
                    },
                    label = { Text(filter) },
                    shape = MaterialTheme.shapes.large
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GooglePhotosTopAppBar(
    searchQuery: String,
    showSearch: Boolean,
    isSelectionMode: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onAddClick: () -> Unit,
    useInContentSearch: Boolean = false,
    onNotificationsClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            if (showSearch) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search your photos") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.google_photos_logo),
                    contentDescription = "Google Photos",
                    modifier = Modifier.height(28.dp)
                )
            }
        },
        actions = {
            // Always show notifications icon (replace the previous Search duplication)
            IconButton(onClick = onNotificationsClick) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
            }
            // When an in-content SearchBar is present we avoid duplicating Add/Profile/Search buttons
            if (!useInContentSearch) {
                if (!isSelectionMode) {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
                // NOTE: Search action intentionally removed from top bar to avoid duplication with in-content SearchBar
                IconButton(onClick = onProfileClick) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "Profile"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun GooglePhotosBottomBar(
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
