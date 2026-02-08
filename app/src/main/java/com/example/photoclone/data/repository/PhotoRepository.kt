package com.example.photoclone.data.repository

import com.example.photoclone.data.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>
    suspend fun refreshPhotos()
    suspend fun toggleFavorite(photoId: Long)
}

class MockPhotoRepository : PhotoRepository {
    private val mockPhotos = List(50) { index ->
        Photo(
            id = index.toLong(),
            imageUrl = "https://picsum.photos/400/400?seed=$index&blur=${index % 5}",
            thumbnailUrl = "https://picsum.photos/200/200?seed=$index",
            dateTaken = System.currentTimeMillis() - (index * 86400000L),
            isFavorite = index % 7 == 0,
            width = 400,
            height = 400
        )
    }.sortedByDescending { it.dateTaken }

    override fun getPhotos(): Flow<List<Photo>> {
        return kotlinx.coroutines.flow.flowOf(mockPhotos)
    }

    override suspend fun refreshPhotos() {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
    }

    override suspend fun toggleFavorite(photoId: Long) {
        // In a real app, update in database
    }
}