package com.example.photoclone.data.repository

import com.example.photoclone.data.model.Photo
import kotlinx.coroutines.flow.Flow

// Repository contract for photos (returns flows and supports refresh/toggle operations).
interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>
    suspend fun refreshPhotos()
    suspend fun toggleFavorite(photoId: Long)
}

// Simple in-memory mock used for UI/dev previews and tests.
class MockPhotoRepository : PhotoRepository {
    // Generate fixed mock photos with seeded URLs and alternating favorites.
    private val mockPhotos = List(50) { index ->
        Photo(
            id = index.toLong(), // mock id; real DB may auto-generate
            imageUrl = "https://picsum.photos/400/400?seed=$index&blur=${index % 5}",
            thumbnailUrl = "https://picsum.photos/200/200?seed=$index",
            dateTaken = System.currentTimeMillis() - (index * 86400000L),
            isFavorite = index % 7 == 0,
            width = 400,
            height = 400
        )
    }.sortedByDescending { it.dateTaken }

    // Return the mock list as a cold flow.
    override fun getPhotos(): Flow<List<Photo>> {
        return kotlinx.coroutines.flow.flowOf(mockPhotos)
    }

    // Simulate a network refresh by delaying.
    override suspend fun refreshPhotos() {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
    }

    // Toggle favorite is a no-op for the mock (would update DB in real repo).
    override suspend fun toggleFavorite(photoId: Long) {
        // In a real app, update database or remote source here.
    }
}