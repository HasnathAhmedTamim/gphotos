package com.example.photoclone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoclone.data.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    // Replace the current list of photos (e.g., when screen data updates)
    fun setPhotos(photos: List<Photo>) {
        _photos.value = photos
        updateSelectionState()
    }

    // Toggle selection for a single photo (tap in selection mode)
    fun toggleSelection(photo: Photo) {
        val currentPhotos = _photos.value.toMutableList()
        val index = currentPhotos.indexOfFirst { it.id == photo.id }
        if (index != -1) {
            currentPhotos[index] = currentPhotos[index].copy(isSelected = !currentPhotos[index].isSelected)
            _photos.value = currentPhotos
            updateSelectionState()
        }
    }

    // Enter selection mode and select the provided photo (used on long-press)
    fun startSelectionMode(photo: Photo) {
        _isSelectionMode.value = true
        val currentPhotos = _photos.value.toMutableList()
        val index = currentPhotos.indexOfFirst { it.id == photo.id }
        if (index != -1) {
            currentPhotos[index] = currentPhotos[index].copy(isSelected = true)
            _photos.value = currentPhotos
            updateSelectionState()
        }
    }

    // New API: set explicit selection state for a photo (used for drag-to-select)
    fun setSelection(photo: Photo, selected: Boolean) {
        val currentPhotos = _photos.value.toMutableList()
        val index = currentPhotos.indexOfFirst { it.id == photo.id }
        if (index != -1 && currentPhotos[index].isSelected != selected) {
            currentPhotos[index] = currentPhotos[index].copy(isSelected = selected)
            _photos.value = currentPhotos
            if (selected) {
                _isSelectionMode.value = true
            }
            updateSelectionState()
        }
    }

    // Clear all selection and exit selection mode
    fun clearSelection() {
        val currentPhotos = _photos.value.map { it.copy(isSelected = false) }
        _photos.value = currentPhotos
        _isSelectionMode.value = false
        _selectedPhotos.value = emptyList()
        _selectedCount.value = 0
    }

    // Internal helper to sync selectedPhotos, selectedCount, and selection mode
    private fun updateSelectionState() {
        val selected = _photos.value.filter { it.isSelected }
        _selectedPhotos.value = selected
        _selectedCount.value = selected.size
        _isSelectionMode.value = selected.isNotEmpty()
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
}
