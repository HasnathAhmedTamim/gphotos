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
    val height: Int = 0,
    val isSelected: Boolean = false,
    val isInAlbum: Boolean = false,
    val isArchived: Boolean = false,
    val isInLockedFolder: Boolean = false,
    val isBackedUp: Boolean = false
)

data class PhotoResponse(
    val photos: List<Photo>,
    val total: Int,
    val page: Int
)