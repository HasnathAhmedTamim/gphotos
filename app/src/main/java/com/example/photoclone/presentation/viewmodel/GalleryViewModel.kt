package com.example.photoclone.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoclone.data.model.Photo
import com.example.photoclone.data.repository.MediaStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for photo gallery
 * Loads photos from device and manages UI state
 */
class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaStoreRepository(application.contentResolver)

    // UI State
    private val _uiState = MutableStateFlow(PhotoUiState())
    val uiState: StateFlow<PhotoUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    /**
     * Load photos from device using MediaStore
     */
    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val photos = repository.loadPhotos()
                _uiState.update {
                    it.copy(
                        photos = photos,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load photos"
                    )
                }
            }
        }
    }

    /**
     * Refresh photos
     */
    fun refresh() {
        loadPhotos()
    }
}

/**
 * UI State for photos screen
 */
data class PhotoUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
