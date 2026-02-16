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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    // Coroutine scope & local buffered selection state to batch updates during drag-to-select
    val coroutineScope = rememberCoroutineScope()
    var selectionFlushJob by remember { mutableStateOf<Job?>(null) }
    // Local buffer used for immediate UI responsiveness while we batch updates to the ViewModel
    // Use a SnapshotStateMap so updating a single entry only recomposes items that read that key.
    val localSelectedUris = remember { mutableStateMapOf<String, Boolean>() }

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

    // If we're using the fallback demo photos, register them into the ViewModel so selection state works
    LaunchedEffect(photoObjects.isEmpty()) {
        if (photoObjects.isEmpty()) {
            // Provide the in-memory displayPhotos to the ViewModel so its selection flows can update UI
            viewModel.setPhotos(displayPhotos)
        }
    }

    // Observe selection state
    val selectedCount by viewModel.selectedCount.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedUris by viewModel.selectedUris.collectAsState()

    // Keep local buffer in sync with ViewModel selection changes. When ViewModel updates (e.g., selection cleared elsewhere), reflect it immediately.
    LaunchedEffect(selectedUris) {
        // Sync the local map with the ViewModel's authoritative selection set.
        localSelectedUris.clear()
        selectedUris.forEach { uri -> localSelectedUris[uri] = true }
    }

    // Lazy grid state used for mapping pointer positions to visible items (drag-to-select)
    val gridState = rememberLazyGridState()

    // Apply selections while preserving the lazy grid's current scroll position.
    fun applySelectionsPreservingScroll(selections: Set<String>) {
        // If nothing changed, skip
        if (viewModel.selectedUris.value == selections) return

        // Capture current scroll position
        val firstIndex = gridState.firstVisibleItemIndex
        val firstOffset = gridState.firstVisibleItemScrollOffset

        // Apply selections and attempt to restore scroll position shortly after to avoid jumps
        coroutineScope.launch {
            try {
                viewModel.setSelections(selections)
            } catch (_: Exception) {
                // ignore
            }

            // Retry restoring scroll position a few times to let layout settle (best-effort)
             val attempts = 6
             var restored = false
            repeat(attempts) {
                 try {
                     gridState.scrollToItem(firstIndex, firstOffset)
                     restored = true
                     return@repeat
                 } catch (_: Exception) {
                     // wait and try again
                 }
                 delay(50L)
             }

             // Final best-effort attempt without catching to surface unexpected issues in debug
             if (!restored) {
                 try {
                     gridState.scrollToItem(firstIndex, firstOffset)
                 } catch (_: Exception) {
                     // ignore
                 }
             }
         }
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

    // Pager window for paging mode: nearby loaded URIs to feed PhotoPager
    var pagerWindow by remember { mutableStateOf<List<String?>>(emptyList()) }

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
            // When permission granted, get paging items (call collectAsLazyPagingItems directly in composition)
            val pagingItems = if (permissionState == true) viewModel.pagerPhotos(context).collectAsLazyPagingItems() else null

            // Determine whether to use paging or the in-memory displayPhotos
            val usingPaging = permissionState == true

            // Render content: pager, loading, empty, or the grid (paged or in-memory)
            if (showPager) {
                val urls = if (usingPaging && pagerWindow.isNotEmpty()) pagerWindow else displayPhotos.map { it.imageUrl }
                PhotoPager(
                    photoUrls = urls,
                    initialPage = selectedPhotoIndex,
                    onDismiss = { showPager = false }
                )
            } else if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (displayPhotos.isEmpty()) {
                // Permission denied or no photos available
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.no_photos), style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(12.dp))

                    // If permission was denied, offer to open app settings
                    val denied = permissionState == false
                    if (denied) {
                        Text(text = "Permission denied. Enable access in settings to view your photos.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }) { Text(text = "Open settings") }

                            // Fallback: let user pick some images instead of granting broad permission
                            Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) }) { Text(text = "Pick images") }
                        }
                    } else {
                        // If permission is simply not yet granted, still offer pick images fallback
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { permissionLauncher.launch(readPermission) }) { Text(text = "Request permission") }
                            Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) }) { Text(text = "Pick images") }
                        }
                    }
                }
            } else {
                // Show grid: paged when permission/grants available, otherwise in-memory grid
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
                                         // toggle selection locally for instant feedback using the map
                                         val uri = p.imageUrl
                                         if (localSelectedUris.containsKey(uri)) localSelectedUris.remove(uri) else localSelectedUris[uri] = true
                                         // schedule a debounced flush to apply the full buffer
                                         selectionFlushJob?.cancel()
                                         selectionFlushJob = coroutineScope.launch {
                                             delay(30)
                                             // push the authoritative buffer to the ViewModel in one update while preserving scroll
                                             applySelectionsPreservingScroll(localSelectedUris.keys.toSet())
                                         }
                                     } else {
                                          // Build a small pager window around the tapped index so the viewer can swipe locally
                                          val windowRadius = 10
                                          // Create a full-size list equal to total items with null placeholders so the pager
                                          // can present the full swipe range even if most pages aren't loaded yet.
                                          val totalItems = pagingItems.itemCount.coerceAtLeast(0)
                                          val fullWindow = MutableList<String?>(totalItems) { null }

                                          // Populate a small neighborhood around the tapped index to show immediately
                                          val start = (index - windowRadius).coerceAtLeast(0)
                                          val end = (index + windowRadius).coerceAtMost(totalItems - 1)
                                          for (i in start..end) {
                                              val item = if (i in 0 until pagingItems.itemCount) pagingItems[i] else null
                                              if (item != null) fullWindow[i] = item.imageUrl
                                          }

                                          pagerWindow = fullWindow
                                          // Use absolute index into the full window so the user can swipe across the whole gallery
                                          selectedPhotoIndex = index.coerceIn(0, pagerWindow.size - 1)
                                           showPager = true
                                      }
                                  },
                                 onLongPress = { if (!isSelectionMode) {
                                     // enter selection and set the buffer atomically
                                     localSelectedUris[p.imageUrl] = true
                                     applySelectionsPreservingScroll(localSelectedUris.keys.toSet())
                                 } },
                                  modifier = itemModifier,
                                  imageRequestSizePx = imageRequestSizePx
                              )
                          }
                      }
                } else {
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
                                    // toggle selection by URI using map for immediate UI feedback
                                    val uri = photo.imageUrl
                                    if (localSelectedUris.containsKey(uri)) localSelectedUris.remove(uri) else localSelectedUris[uri] = true
                                    selectionFlushJob?.cancel()
                                    selectionFlushJob = coroutineScope.launch {
                                        delay(30)
                                        applySelectionsPreservingScroll(localSelectedUris.keys.toSet())
                                    }
                                } else {
                                    selectedPhotoIndex = index
                                    showPager = true
                                }
                            },
                            onLongPress = { if (!isSelectionMode) {
                                localSelectedUris[photo.imageUrl] = true
                                applySelectionsPreservingScroll(localSelectedUris.keys.toSet())
                            } },
                            modifier = itemModifier,
                            imageRequestSizePx = imageRequestSizePx
                         )
                     }
                 }
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
                val pagingCount = if (usingPaging && pagingItems != null) pagingItems.itemCount else -1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "perm:${permissionState}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "photos:${displayPhotos.size}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "pagerItems:${pagingCount}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Button(onClick = { viewModel.loadGallery(context) }) { Text("Reload") }
                }

                // Extra debug info when pager is visible: show pagerWindow size and selected index so we can diagnose swipe issues
                if (showPager) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                            .padding(8.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Text(text = "pagerWindow:${pagerWindow.size} sel:$selectedPhotoIndex", style = MaterialTheme.typography.labelSmall)
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
