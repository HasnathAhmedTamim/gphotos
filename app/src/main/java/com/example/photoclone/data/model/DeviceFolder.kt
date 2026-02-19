package com.example.photoclone.data.model

/**
 * Represents a device folder containing media files
 */
data class DeviceFolder(
    val id: String,
    val name: String,
    val path: String,
    val itemCount: Int,
    val thumbnailUri: String? = null
)
