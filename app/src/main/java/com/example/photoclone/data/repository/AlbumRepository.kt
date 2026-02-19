package com.example.photoclone.data.repository

import com.example.photoclone.data.local.AlbumDao
import com.example.photoclone.data.local.AlbumEntity
import com.example.photoclone.data.local.AlbumPhotoEntity
import com.example.photoclone.presentation.model.AlbumItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Repository for managing albums
 */
class AlbumRepository(
    private val albumDao: AlbumDao
) {

    /**
     * Get all albums as Flow for reactive updates
     */
    fun getAllAlbumsFlow(): Flow<List<AlbumItem>> {
        return albumDao.getAllAlbumsFlow().map { entities ->
            entities.map { entity ->
                val photoCount = albumDao.getPhotoCountInAlbum(entity.id)
                AlbumItem(
                    id = entity.id,
                    title = entity.name,
                    itemCount = photoCount,
                    thumbnailUrl = entity.coverPhotoUri
                )
            }
        }
    }

    /**
     * Get all albums (one-time)
     */
    suspend fun getAllAlbums(): List<AlbumItem> {
        val entities = albumDao.getAllAlbums()
        return entities.map { entity ->
            val photoCount = albumDao.getPhotoCountInAlbum(entity.id)
            AlbumItem(
                id = entity.id,
                title = entity.name,
                itemCount = photoCount,
                thumbnailUrl = entity.coverPhotoUri
            )
        }
    }

    /**
     * Create a new album
     */
    suspend fun createAlbum(name: String): String {
        val albumId = UUID.randomUUID().toString()
        val album = AlbumEntity(
            id = albumId,
            name = name,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        albumDao.insertAlbum(album)
        return albumId
    }

    /**
     * Delete an album
     */
    suspend fun deleteAlbum(albumId: String) {
        albumDao.deleteAlbumById(albumId)
        albumDao.removeAllPhotosFromAlbum(albumId)
    }

    /**
     * Rename an album
     */
    suspend fun renameAlbum(albumId: String, newName: String) {
        val album = albumDao.getAlbumById(albumId) ?: return
        albumDao.updateAlbum(
            album.copy(
                name = newName,
                modifiedAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Add photos to an album
     */
    suspend fun addPhotosToAlbum(albumId: String, photoUris: List<String>) {
        val timestamp = System.currentTimeMillis()
        photoUris.forEach { uri ->
            albumDao.insertAlbumPhoto(
                AlbumPhotoEntity(
                    albumId = albumId,
                    photoUri = uri,
                    addedAt = timestamp
                )
            )
        }

        // Update album's modified time
        val album = albumDao.getAlbumById(albumId) ?: return
        albumDao.updateAlbum(album.copy(modifiedAt = timestamp))

        // Update cover photo if not set
        if (album.coverPhotoUri == null && photoUris.isNotEmpty()) {
            albumDao.updateAlbum(album.copy(coverPhotoUri = photoUris.first()))
        }
    }

    /**
     * Remove a photo from an album
     */
    suspend fun removePhotoFromAlbum(albumId: String, photoUri: String) {
        albumDao.removePhotoFromAlbum(albumId, photoUri)

        // Update album's modified time
        val album = albumDao.getAlbumById(albumId) ?: return
        albumDao.updateAlbum(album.copy(modifiedAt = System.currentTimeMillis()))
    }

    /**
     * Get photos in an album
     */
    suspend fun getPhotosInAlbum(albumId: String): List<String> {
        return albumDao.getPhotosInAlbum(albumId).map { it.photoUri }
    }

    /**
     * Update album cover photo
     */
    suspend fun updateAlbumCover(albumId: String, photoUri: String) {
        val album = albumDao.getAlbumById(albumId) ?: return
        albumDao.updateAlbum(
            album.copy(
                coverPhotoUri = photoUri,
                modifiedAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Get album details
     */
    suspend fun getAlbumDetails(albumId: String): AlbumItem? {
        val entity = albumDao.getAlbumById(albumId) ?: return null
        val photoCount = albumDao.getPhotoCountInAlbum(albumId)
        return AlbumItem(
            id = entity.id,
            title = entity.name,
            itemCount = photoCount,
            thumbnailUrl = entity.coverPhotoUri
        )
    }
}
