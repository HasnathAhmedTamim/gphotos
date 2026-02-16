package com.example.photoclone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Photo entity for Room.
 * Short notes inline – keep UI-only state out of the DB.
 */
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Main image URI (non-null expected).
    val imageUrl: String,

    // Optional thumbnail URI.
    val thumbnailUrl: String? = null,

    // Time taken, stored as epoch millis (Long). Consider Instant + TypeConverter.
    val dateTaken: Long = System.currentTimeMillis(),

    // Persisted favorite flag.
    val isFavorite: Boolean = false,

    // Image dimensions in pixels. Use nullable Int? if unknown.
    val width: Int = 0,
    val height: Int = 0,

    // UI-only (selection). Do NOT persist; move to a UI model or mark @Ignore.
    val isSelected: Boolean = false,

    // Simple album flag; use a join table for many-to-many relationships.
    val isInAlbum: Boolean = false,

    // Persisted archived flag.
    val isArchived: Boolean = false,

    // Sensitive flag (locked folder) — avoid exporting this field unintentionally.
    val isInLockedFolder: Boolean = false,

    // Persisted backup status.
    val isBackedUp: Boolean = false
)

// Simple paged response. Consider separate network DTOs to avoid coupling.
data class PhotoResponse(
    val photos: List<Photo>,
    val total: Int,
    val page: Int
)