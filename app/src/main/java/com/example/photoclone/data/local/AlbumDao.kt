package com.example.photoclone.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Album operations
 */
@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums ORDER BY modifiedAt DESC")
    fun getAllAlbumsFlow(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums ORDER BY modifiedAt DESC")
    suspend fun getAllAlbums(): List<AlbumEntity>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: String): AlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Update
    suspend fun updateAlbum(album: AlbumEntity)

    @Delete
    suspend fun deleteAlbum(album: AlbumEntity)

    @Query("DELETE FROM albums WHERE id = :albumId")
    suspend fun deleteAlbumById(albumId: String)

    // Album-Photo operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumPhoto(albumPhoto: AlbumPhotoEntity)

    @Query("SELECT * FROM album_photos WHERE albumId = :albumId ORDER BY addedAt DESC")
    suspend fun getPhotosInAlbum(albumId: String): List<AlbumPhotoEntity>

    @Query("DELETE FROM album_photos WHERE albumId = :albumId AND photoUri = :photoUri")
    suspend fun removePhotoFromAlbum(albumId: String, photoUri: String)

    @Query("DELETE FROM album_photos WHERE albumId = :albumId")
    suspend fun removeAllPhotosFromAlbum(albumId: String)

    @Query("SELECT COUNT(*) FROM album_photos WHERE albumId = :albumId")
    suspend fun getPhotoCountInAlbum(albumId: String): Int

    @Query("SELECT photoUri FROM album_photos WHERE albumId = :albumId ORDER BY addedAt DESC LIMIT 1")
    suspend fun getLatestPhotoInAlbum(albumId: String): String?
}
