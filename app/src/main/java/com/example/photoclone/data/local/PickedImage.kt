package com.example.photoclone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted user-picked images (from SAF) so the app can show them across launches without broad storage permission.
 */
@Entity(tableName = "picked_images")
data class PickedImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val addedAt: Long = System.currentTimeMillis()
)
