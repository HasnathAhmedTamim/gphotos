package com.example.photoclone.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoclone.data.model.Photo
import com.example.photoclone.data.repository.GalleryRepository
import com.example.photoclone.data.repository.PickedImagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

/**
 *  ViewModel for managing photo selection state across UI screens. It holds the list of photos,
 *  tracks which photos are selected, and provides functions to toggle selection, enter/exit selection mode, and perform batch actions on selected items. The ViewModel uses StateFlows to expose state to the UI, allowing composables to reactively update based on selection changes. This centralized management of selection state ensures consistency across different screens (e.g., grid and detail) and supports features like drag-to-select and batch operations in the bottom sheet.
 * Key responsibilities:
 * - Maintain the list of photos and their selection state.
 * - Provide functions to toggle selection for individual photos and to enter/exit selection mode.
 * - Keep track of the count of selected items for UI badges and sheet visibility.
 * - Provide functions to perform batch actions (archive, delete, share, etc.) on selected items, which can be wired to the data layer for actual operations.
 * - Ensure that selection mode is enabled when items are selected and disabled when selection is cleared.
 * The ViewModel is designed to be used across multiple screens, allowing for a consistent selection experience whether the user is in the photo grid or viewing a single photo in detail. By centralizing selection logic here, we can easily manage complex interactions like drag-to-select and ensure that the UI reflects the current selection state accurately.
 *
 * */

// ViewModel that manages photo list and selection state for UI screens.
@Suppress("unused")
class PhotoSelectionViewModel : ViewModel() {

    // All photos shown in the UI (source of truth for selection flag)
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    // Explicit list of selected photos (kept in sync by updateSelectionState)
    @Suppress("unused")
    private val _selectedPhotos = MutableStateFlow<List<Photo>>(emptyList())
    @Suppress("unused")
    val selectedPhotos: StateFlow<List<Photo>> = _selectedPhotos.asStateFlow()

    // Count of selected items (useful for UI badges and sheet visibility)
    private val _selectedCount = MutableStateFlow(0)
    val selectedCount: StateFlow<Int> = _selectedCount.asStateFlow()

    // Whether selection mode (multi-select) is active
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    // Track selected URIs (works with paging where Photo instances are ephemeral)
    private val _selectedUris = MutableStateFlow<Set<String>>(emptySet())
    val selectedUris: StateFlow<Set<String>> = _selectedUris.asStateFlow()

    // Job for managing the coroutine that collects picked images
    private var pickedImagesCollectJob: Job? = null

    // Update selection state flows to reflect selectedUris as the source of truth for selection count/mode
    private fun syncSelectionFromUris() {
        // Update selectedCount and isSelectionMode based on selectedUris
        _selectedCount.value = _selectedUris.value.size
        _isSelectionMode.value = _selectedUris.value.isNotEmpty()
        // Also update _selectedPhotos for non-paged mode to keep UI consistent
        val photos = _photos.value
        if (photos.isNotEmpty()) {
            // Do NOT mutate the _photos list here. Instead, compute the selected subset from
            // the current photos and the selectedUris set. This avoids wholesale list replacements
            // that can cause Lazy grids to rebind and jump.
            val selectedSet = _selectedUris.value
            _selectedPhotos.value = photos.filter { selectedSet.contains(it.imageUrl) }
        } else {
            _selectedPhotos.value = emptyList()
        }
    }

    // Replace the current list of photos (e.g., when screen data updates)
    fun setPhotos(photos: List<Photo>) {
        val updated = photos.map { p ->
            if (_selectedUris.value.contains(p.imageUrl)) p.copy(isSelected = true) else p.copy(isSelected = false)
        }
        _photos.value = updated
        updateSelectionState()
    }

    // Load gallery images from device and populate photo list (called after permission is granted)
    fun loadGallery(context: Context, limit: Int = 200) {
        // If we previously were showing persisted picks, stop that collector when loading full gallery
        pickedImagesCollectJob?.cancel()
        pickedImagesCollectJob = null
        viewModelScope.launch {
            try {
                // Use applicationContext to avoid accidentally retaining an Activity context
                val appContext = context.applicationContext
                val uris = GalleryRepository.loadGalleryImageUris(appContext, limit)
                val photoList = uris.mapIndexed { index, uri ->
                    Photo(
                        id = index.toLong(),
                        imageUrl = uri.toString(),
                        thumbnailUrl = uri.toString()
                    )
                }
                _photos.value = photoList
                updateSelectionState()
            } catch (_: Exception) {
                // Ignore or log; keep existing photos
            }
        }
    }

    // Toggle selection by Photo object (keeps old API compatible)
    fun toggleSelection(photo: Photo) {
        toggleSelectionByUri(photo.imageUrl)
    }

    // Start selection by Photo (old API)
    fun startSelectionMode(photo: Photo) {
        setSelectionByUri(photo.imageUrl, true)
    }

    // New API: toggle selection by Uri (works for paging)
    fun toggleSelectionByUri(uri: String) {
        val current = _selectedUris.value.toMutableSet()
        if (current.contains(uri)) current.remove(uri) else current.add(uri)
        _selectedUris.value = current
        syncSelectionFromUris()
    }

    // New API: set selection by Uri
    fun setSelectionByUri(uri: String, selected: Boolean) {
        val current = _selectedUris.value.toMutableSet()
        val changed = if (selected) current.add(uri) else current.remove(uri)
        if (changed) {
            _selectedUris.value = current
            syncSelectionFromUris()
        }
    }

    /**
     * Add multiple URIs to the current selection in one atomic update. This reduces the number
     * of StateFlow emissions and recompositions when selecting items in batches (e.g., drag-to-select).
     */
    fun addSelections(uris: Set<String>) {
        if (uris.isEmpty()) return
        val current = _selectedUris.value.toMutableSet()
        val changed = current.addAll(uris)
        if (changed) {
            _selectedUris.value = current
            syncSelectionFromUris()
        }
    }

    /**
     * Replace the current selection set with the provided set in one atomic update.
     * Use this when the UI maintains a local buffered selection and wants to push the full set
     * to the ViewModel to avoid multiple incremental updates.
     */
    fun setSelections(uris: Set<String>) {
        if (_selectedUris.value == uris) return
        _selectedUris.value = uris.toSet()
        syncSelectionFromUris()
    }

    // Backwards-compatible API: set selection by Photo instance
    fun setSelection(photo: Photo, selected: Boolean) {
        setSelectionByUri(photo.imageUrl, selected)
    }

    // Clear all selection and exit selection mode
    fun clearSelection() {
        // Clear selected URIs (source of truth) and sync derived state
        _selectedUris.value = emptySet()
        syncSelectionFromUris()
    }

    // Internal helper to sync selectedPhotos, selectedCount, and selection mode
    private fun updateSelectionState() {
        // Delegate to the central sync routine which uses _selectedUris as source of truth
        syncSelectionFromUris()
    }

    // Archive selected items, then clear selection (TODO: wire to data layer)
    fun archiveSelected() {
        viewModelScope.launch {
            // TODO: Implement archive logic
            clearSelection()
        }
    }

    // Delete selected items, then clear selection (TODO: wire to data layer)
    fun deleteSelected() {
        viewModelScope.launch {
            // TODO: Implement delete logic
            clearSelection()
        }
    }

    // Trigger share flow for selected items (TODO: implement sharing)
    fun shareSelected() {
        viewModelScope.launch {
            // TODO: Implement share logic
        }
    }

    // Add selected items to an album (TODO: implement)
    fun addToAlbum() {
        viewModelScope.launch {
            // TODO: Implement add to album logic
        }
    }

    // Back up selected items (TODO: implement)
    fun backupSelected() {
        viewModelScope.launch {
            // TODO: Implement backup logic
        }
    }

    // Move selected items to locked folder (TODO: implement)
    fun moveToLockedFolder() {
        viewModelScope.launch {
            // TODO: Implement move to locked folder logic
        }
    }

    // New: provide a paging flow of Photos for large galleries
    fun pagerPhotos(context: Context, pageSize: Int = 50): Flow<PagingData<Photo>> {
        // Use applicationContext to avoid Activity retention
        val appContext = context.applicationContext
        // GalleryRepository.pagerForImages already returns Flow<PagingData<Photo>> so just cache it here
        return GalleryRepository.pagerForImages(appContext, pageSize)
            .cachedIn(viewModelScope)
    }

    // Persist a list of picked Uris (SAF) using PickedImagesRepository
    fun persistPickedUris(context: Context, uris: List<String>) {
        // Use applicationContext here to avoid leaking a UI context
        val repo = PickedImagesRepository(context.applicationContext)
        viewModelScope.launch {
            uris.forEach { uri ->
                try {
                    repo.savePickedUri(uri)
                } catch (_: Exception) {
                    // ignore
                }
            }
            // Reload persisted picks into view model
            loadPickedImages(context)
        }
    }

    // Load persisted picked images from local DB (Room) and set them as photos
    fun loadPickedImages(context: Context) {
        val repo = PickedImagesRepository(context.applicationContext)
        // Cancel any prior job
        pickedImagesCollectJob?.cancel()
        pickedImagesCollectJob = viewModelScope.launch {
            repo.getPickedImages().collect { list ->
                val mapped = list.map { picked ->
                    Photo(id = picked.id, imageUrl = picked.uri, thumbnailUrl = picked.uri)
                }
                _photos.value = mapped
                updateSelectionState()
            }
        }
    }
}
