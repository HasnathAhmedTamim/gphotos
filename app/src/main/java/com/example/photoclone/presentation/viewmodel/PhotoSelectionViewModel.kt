package com.example.photoclone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoclone.data.model.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoSelectionViewModel : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    @Suppress("unused")
    private val _selectedPhotos = MutableStateFlow<List<Photo>>(emptyList())
    @Suppress("unused")
    val selectedPhotos: StateFlow<List<Photo>> = _selectedPhotos.asStateFlow()

    private val _selectedCount = MutableStateFlow(0)
    val selectedCount: StateFlow<Int> = _selectedCount.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    fun setPhotos(photos: List<Photo>) {
        _photos.value = photos
    }

    fun toggleSelection(photo: Photo) {
        viewModelScope.launch {
            val currentPhotos = _photos.value.toMutableList()
            val index = currentPhotos.indexOfFirst { it.id == photo.id }
            if (index != -1) {
                currentPhotos[index] = currentPhotos[index].copy(isSelected = !currentPhotos[index].isSelected)
                _photos.value = currentPhotos
                updateSelectionState()
            }
        }
    }

    fun startSelectionMode(photo: Photo) {
        viewModelScope.launch {
            _isSelectionMode.value = true
            val currentPhotos = _photos.value.toMutableList()
            val index = currentPhotos.indexOfFirst { it.id == photo.id }
            if (index != -1) {
                currentPhotos[index] = currentPhotos[index].copy(isSelected = true)
                _photos.value = currentPhotos
                updateSelectionState()
            }
        }
    }

    // New API: set explicit selection state for a photo (used for drag-to-select)
    fun setSelection(photo: Photo, selected: Boolean) {
        viewModelScope.launch {
            val currentPhotos = _photos.value.toMutableList()
            val index = currentPhotos.indexOfFirst { it.id == photo.id }
            if (index != -1 && currentPhotos[index].isSelected != selected) {
                currentPhotos[index] = currentPhotos[index].copy(isSelected = selected)
                _photos.value = currentPhotos
                // Ensure selection mode is enabled when selecting
                if (selected) {
                    _isSelectionMode.value = true
                }
                updateSelectionState()
            }
        }
    }

    fun clearSelection() {
        viewModelScope.launch {
            val currentPhotos = _photos.value.map { it.copy(isSelected = false) }
            _photos.value = currentPhotos
            _isSelectionMode.value = false
            _selectedPhotos.value = emptyList()
            _selectedCount.value = 0
        }
    }

    private fun updateSelectionState() {
        val selected = _photos.value.filter { it.isSelected }
        _selectedPhotos.value = selected
        _selectedCount.value = selected.size
        _isSelectionMode.value = selected.isNotEmpty()
    }

    fun archiveSelected() {
        viewModelScope.launch {
            // TODO: Implement archive logic
            clearSelection()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            // TODO: Implement delete logic
            clearSelection()
        }
    }

    fun shareSelected() {
        viewModelScope.launch {
            // TODO: Implement share logic
        }
    }

    fun addToAlbum() {
        viewModelScope.launch {
            // TODO: Implement add to album logic
        }
    }

    fun backupSelected() {
        viewModelScope.launch {
            // TODO: Implement backup logic
        }
    }

    fun moveToLockedFolder() {
        viewModelScope.launch {
            // TODO: Implement move to locked folder logic
        }
    }
}
