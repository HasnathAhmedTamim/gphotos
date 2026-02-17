@file:Suppress("ALL")

package com.example.photoclone.presentation.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.OpenMultipleDocuments
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoclone.R
import com.example.photoclone.data.model.Photo
import com.example.photoclone.presentation.components.*
import com.example.photoclone.presentation.viewmodel.PhotoSelectionViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlin.runCatching
import kotlinx.coroutines.launch

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
    val context = LocalContext.current

    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()

    // Local buffered selection state - use SnapshotStateMap for efficient single-entry updates
    val localSelectedUris = remember { mutableStateMapOf<String, Boolean>() }

    // ========================================================================
    // REAL GALLERY FUNCTIONALITY - COMMENTED OUT FOR MOCK IMAGE USAGE
    // TODO: Uncomment the section below when ready to use real device gallery
    // ========================================================================

    /*
    // Permission logic: choose the correct runtime permission for the SDK
    val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var permissionState by remember { mutableStateOf<Boolean?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionState = granted
        if (granted) {
            viewModel.loadGallery(context)
        }
    }

    // Launcher for picking multiple images as a fallback when broad permission is not granted
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            // Take persistable URI permissions and persist the URIs
            val uriStrings = mutableListOf<String>()
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) {
                    // ignore
                }
                uriStrings += uri.toString()
            }
            // Persist via ViewModel (saves to Room)
            viewModel.persistPickedUris(context, uriStrings)
        }
    }

    // On first composition, check permission and request if needed
    LaunchedEffect(Unit) {
        val granted = androidx.core.content.ContextCompat.checkSelfPermission(context, readPermission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        permissionState = granted
        if (granted) {
            viewModel.loadGallery(context)
        } else {
            // Load any previously picked images (persisted via SAF) so the grid can show them without broad permission
            viewModel.loadPickedImages(context)
            // Launch permission request; you might want to show rationale first in a production app
            permissionLauncher.launch(readPermission)
        }
    }

    // Observe photos from the ViewModel; fallback to the provided demo list if empty
    val photoObjects by viewModel.photos.collectAsState()
    val displayPhotos = if (photoObjects.isNotEmpty()) photoObjects else photos.mapIndexed { index, url ->
        Photo(id = index.toLong(), imageUrl = url, thumbnailUrl = url)
    }
    */

    // ========================================================================
    // MOCK IMAGE MODE - CURRENTLY ACTIVE
    // Using mock/demo images provided via the photos parameter
    // ========================================================================

    // Force permission state to false to disable real gallery loading
    val permissionState: Boolean? = false

    // Always use mock images from the photos parameter
    val displayPhotos = photos.mapIndexed { index, url ->
        Photo(id = index.toLong(), imageUrl = url, thumbnailUrl = url)
    }

    // Register mock photos with ViewModel for selection state tracking
    LaunchedEffect(displayPhotos) {
        if (displayPhotos.isNotEmpty()) {
            viewModel.setPhotos(displayPhotos)
        }
    }

    // Observe selection state
    val selectedCount by viewModel.selectedCount.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedUris by viewModel.selectedUris.collectAsState()

    // Sync local buffer with ViewModel ONLY when cleared externally (e.g., via clear button)
    // This prevents flickering from constant rebuilds while maintaining consistency
    LaunchedEffect(selectedUris.isEmpty()) {
        if (selectedUris.isEmpty() && localSelectedUris.isNotEmpty()) {
            localSelectedUris.clear()
        }
    }

    // Lazy grid state used for mapping pointer positions to visible items (drag-to-select)
    val gridState = rememberLazyGridState()

    // Apply selections to ViewModel in a single batch update
    fun applySelections(selections: Set<String>) {
        // If nothing changed, skip
        if (viewModel.selectedUris.value == selections) return
        viewModel.setSelections(selections)
    }

    // Derived state: show selection bottom sheet only when in selection mode and items selected
    val showBottomSheet by remember(selectedCount, isSelectionMode) {
        derivedStateOf { isSelectionMode && selectedCount > 0 }
    }

    // Pager controls for full-screen image viewer
    var showPager by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

    // Columns count adjustable via pinch gestures
    var columns by remember { mutableStateOf(3) }

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
            // Only render a top bar when the pager is not visible
            if (!showPager) {
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
            // ========================================================================
            // PAGING LOGIC - COMMENTED OUT (for real gallery)
            // TODO: Uncomment when using real device gallery
            // ========================================================================
            /*
            // When permission granted, get paging items (call collectAsLazyPagingItems directly in composition)
            val pagingItems = if (permissionState == true) viewModel.pagerPhotos(context).collectAsLazyPagingItems() else null

            // Determine whether to use paging or the in-memory displayPhotos
            val usingPaging = permissionState == true
            */

            // Force using in-memory mock images (no paging)
            val usingPaging = false
            val pagingItems = null

            // Render content: pager, loading, empty, or the grid
            if (showPager) {
                // For mock images: pass full list directly (no dynamic loading needed)
                val urls = displayPhotos.map { it.imageUrl }
                PhotoPager(
                    photoUrls = urls,
                    initialPage = selectedPhotoIndex,
                    onDismiss = { showPager = false }
                )
            } else if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (displayPhotos.isEmpty()) {
                // No photos available (should not happen with mock images)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.no_photos), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                // ========================================================================
                // PAGING GRID - COMMENTED OUT (for real gallery)
                // ========================================================================
                /*
                if (usingPaging && pagingItems != null) {
                    AdaptivePagingGrid(
                        items = pagingItems,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoom, _ ->
                                    // Apply a small threshold to avoid oscillation on tiny pinch gestures
                                    if (zoom > 1.05f) {
                                        columns = (columns - 1).coerceAtLeast(2)
                                    } else if (zoom < 0.95f) {
                                        columns = (columns + 1).coerceAtMost(6)
                                    }
                                }
                            },
                         contentPadding = PaddingValues(4.dp),
                         gutter = 4.dp,
                         columns = columns,
                         state = gridState,
                         // Make every 7th item a larger tile (mimics Google Photos masonry feel)
                         bigItemPredicate = { index, _ -> index % 7 == 0 }
                     ) { index, photo, imageRequestSizePx, itemModifier ->
                         photo?.let { p ->
                            SelectablePhotoGridItem(
                                imageUrl = p.imageUrl,
                                isSelected = localSelectedUris.containsKey(p.imageUrl),
                                isSelectionMode = isSelectionMode,
                                onClick = {
                                     if (isSelectionMode) {
                                         // Toggle selection locally for instant visual feedback
                                         val uri = p.imageUrl
                                         if (localSelectedUris.containsKey(uri)) {
                                             localSelectedUris.remove(uri)
                                         } else {
                                             localSelectedUris[uri] = true
                                         }
                                         // Immediately apply to ViewModel (no debouncing)
                                         applySelections(localSelectedUris.keys.toSet())
                                     } else {
                                          // Enter full-screen pager mode - dynamic loading will handle fetching pages
                                          selectedPhotoIndex = index.coerceIn(0, pagingItems.itemCount - 1)
                                          showPager = true
                                      }
                                  },
                                 onLongPress = {
                                     if (!isSelectionMode) {
                                         // Enter selection mode and select this item
                                         localSelectedUris[p.imageUrl] = true
                                         applySelections(localSelectedUris.keys.toSet())
                                     }
                                 },
                                  modifier = itemModifier,
                                  imageRequestSizePx = imageRequestSizePx
                              )
                          }
                      }
                } else {
                */

                // ========================================================================
                // IN-MEMORY GRID - ACTIVE (for mock images)
                // ========================================================================
                AdaptivePhotoGrid(
                         photos = displayPhotos,
                         modifier = Modifier
                             .fillMaxSize()
                             .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoom, _ ->
                                    if (zoom > 1.05f) columns = (columns - 1).coerceAtLeast(2) else if (zoom < 0.95f) columns = (columns + 1).coerceAtMost(6)
                                }
                             },
                         contentPadding = PaddingValues(4.dp),
                         gutter = 4.dp,
                         columns = columns,
                         state = gridState,
                         // same treatment for in-memory list: make every 7th item a large tile
                         bigItemPredicate = { index, _ -> index % 7 == 0 }
                     ) { index, photo, imageRequestSizePx, itemModifier ->
                         SelectablePhotoGridItem(
                            imageUrl = photo.imageUrl,
                            isSelected = localSelectedUris.containsKey(photo.imageUrl),
                            isSelectionMode = isSelectionMode,
                            onClick = {
                                if (isSelectionMode) {
                                    // Toggle selection locally for instant visual feedback
                                    val uri = photo.imageUrl
                                    if (localSelectedUris.containsKey(uri)) {
                                        localSelectedUris.remove(uri)
                                    } else {
                                        localSelectedUris[uri] = true
                                    }
                                    // Immediately apply to ViewModel (no debouncing)
                                    applySelections(localSelectedUris.keys.toSet())
                                } else {
                                    selectedPhotoIndex = index
                                    showPager = true
                                }
                            },
                            onLongPress = {
                                if (!isSelectionMode) {
                                    // Enter selection mode and select this item
                                    localSelectedUris[photo.imageUrl] = true
                                    applySelections(localSelectedUris.keys.toSet())
                                }
                            },
                            modifier = itemModifier,
                            imageRequestSizePx = imageRequestSizePx
                         )
                     }
                // End of AdaptivePhotoGrid
            }

            // Debug banner to help diagnose device issues (shown only when BuildConfig.DEBUG is true).
            val debugMode = remember {
                runCatching {
                    val cls = Class.forName("${context.packageName}.BuildConfig")
                    val field = cls.getDeclaredField("DEBUG")
                    field.isAccessible = true
                    (field.get(null) as? Boolean) ?: false
                }.getOrDefault(false)
            }

            if (debugMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "MODE: MOCK IMAGES", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "photos:${displayPhotos.size}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "selected:$selectedCount", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
