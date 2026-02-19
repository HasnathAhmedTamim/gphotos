package com.example.photoclone.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoclone.data.repository.AlbumRepository
import com.example.photoclone.presentation.model.AlbumItem
import com.example.photoclone.presentation.model.CategoryItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Sorting options for albums
 */
enum class AlbumSortOption {
    RECENT,      // Recently modified
    NAME_ASC,    // A to Z
    NAME_DESC,   // Z to A
    SIZE_DESC,   // Most items first
    SIZE_ASC     // Least items first
}

/**
 * UI State for Collections Screen
 */
data class CollectionsUiState(
    val albums: List<AlbumItem> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0, // 0 = Albums, 1 = Device Folders
    val isSelectionMode: Boolean = false,
    val selectedAlbums: Set<String> = emptySet(),
    val sortOption: AlbumSortOption = AlbumSortOption.RECENT,
    val isAlbumsSectionExpanded: Boolean = true,
    val searchQuery: String = ""
)

/**
 * Production-ready ViewModel for Collections Screen
 */
class CollectionsViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    // Unified UI State
    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    init {
        loadAlbums()
        loadCategories()
    }

    /**
     * Load categories (static for now)
     */
    private fun loadCategories() {
        val categories = listOf(
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
        _uiState.update { it.copy(categories = categories) }
    }

    /**
     * Load all albums from repository
     */
    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                albumRepository.getAllAlbumsFlow().collect { albumList ->
                    val sortedAndFiltered = albumList
                        .filter { album ->
                            if (_uiState.value.searchQuery.isBlank()) {
                                true
                            } else {
                                album.title.contains(_uiState.value.searchQuery, ignoreCase = true)
                            }
                        }
                        .let { sortAlbums(it, _uiState.value.sortOption) }

                    _uiState.update {
                        it.copy(
                            albums = sortedAndFiltered,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load albums"
                    )
                }
            }
        }
    }

    /**
     * Sort albums based on selected option
     */
    private fun sortAlbums(albums: List<AlbumItem>, option: AlbumSortOption): List<AlbumItem> {
        return when (option) {
            AlbumSortOption.RECENT -> albums // Already sorted by modified date in repo
            AlbumSortOption.NAME_ASC -> albums.sortedBy { it.title.lowercase() }
            AlbumSortOption.NAME_DESC -> albums.sortedByDescending { it.title.lowercase() }
            AlbumSortOption.SIZE_DESC -> albums.sortedByDescending { it.itemCount }
            AlbumSortOption.SIZE_ASC -> albums.sortedBy { it.itemCount }
        }
    }

    /**
     * Create a new album
     */
    fun createAlbum(name: String, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val albumId = albumRepository.createAlbum(name)
                onSuccess(albumId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to create album") }
            }
        }
    }

    /**
     * Delete selected albums
     */
    fun deleteSelectedAlbums() {
        viewModelScope.launch {
            try {
                _uiState.value.selectedAlbums.forEach { albumId ->
                    albumRepository.deleteAlbum(albumId)
                }
                exitSelectionMode()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to delete albums") }
            }
        }
    }

    /**
     * Delete a single album
     */
    fun deleteAlbum(albumId: String) {
        viewModelScope.launch {
            try {
                albumRepository.deleteAlbum(albumId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to delete album") }
            }
        }
    }

    /**
     * Rename an album
     */
    fun renameAlbum(albumId: String, newName: String) {
        viewModelScope.launch {
            try {
                albumRepository.renameAlbum(albumId, newName)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to rename album") }
            }
        }
    }

    /**
     * Update album cover
     */
    fun updateAlbumCover(albumId: String, photoUri: String) {
        viewModelScope.launch {
            try {
                albumRepository.updateAlbumCover(albumId, photoUri)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to update cover") }
            }
        }
    }

    // ============ Selection Mode ============

    /**
     * Enter selection mode with an album
     */
    fun enterSelectionMode(albumId: String) {
        _uiState.update {
            it.copy(
                isSelectionMode = true,
                selectedAlbums = setOf(albumId)
            )
        }
    }

    /**
     * Toggle album selection
     */
    fun toggleAlbumSelection(albumId: String) {
        _uiState.update { state ->
            val newSelection = if (albumId in state.selectedAlbums) {
                state.selectedAlbums - albumId
            } else {
                state.selectedAlbums + albumId
            }

            // Exit selection mode if no albums selected
            if (newSelection.isEmpty()) {
                state.copy(
                    selectedAlbums = newSelection,
                    isSelectionMode = false
                )
            } else {
                state.copy(selectedAlbums = newSelection)
            }
        }
    }

    /**
     * Select all albums
     */
    fun selectAllAlbums() {
        _uiState.update { state ->
            state.copy(
                selectedAlbums = state.albums.map { it.id }.toSet(),
                isSelectionMode = true
            )
        }
    }

    /**
     * Exit selection mode
     */
    fun exitSelectionMode() {
        _uiState.update {
            it.copy(
                isSelectionMode = false,
                selectedAlbums = emptySet()
            )
        }
    }

    // ============ UI State Management ============

    /**
     * Switch tab
     */
    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    /**
     * Toggle albums section expansion
     */
    fun toggleAlbumsExpansion() {
        _uiState.update { it.copy(isAlbumsSectionExpanded = !it.isAlbumsSectionExpanded) }
    }

    /**
     * Update sort option
     */
    fun updateSortOption(option: AlbumSortOption) {
        _uiState.update { state ->
            state.copy(
                sortOption = option,
                albums = sortAlbums(state.albums, option)
            )
        }
    }

    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadAlbums() // Reload with filter
    }

    /**
     * Clear search
     */
    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
        loadAlbums()
    }

    /**
     * Clear error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Refresh albums
     */
    fun refresh() {
        loadAlbums()
    }
}
