package com.example.photoclone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Albums
 */
@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long,
    val modifiedAt: Long,
    val coverPhotoUri: String? = null,
    val isShared: Boolean = false
)

/**
 * Junction table for Album-Photo relationship (many-to-many)
 */
@Entity(
    tableName = "album_photos",
    primaryKeys = ["albumId", "photoUri"]
)
data class AlbumPhotoEntity(
    val albumId: String,
    val photoUri: String,
    val addedAt: Long
)
