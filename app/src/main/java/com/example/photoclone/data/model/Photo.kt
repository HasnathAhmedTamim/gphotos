package com.example.photoclone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUrl: String,
    val thumbnailUrl: String? = null,
    val dateTaken: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val width: Int = 0,
    val height: Int = 0
)

data class PhotoResponse(
    val photos: List<Photo>,
    val total: Int,
    val page: Int
)