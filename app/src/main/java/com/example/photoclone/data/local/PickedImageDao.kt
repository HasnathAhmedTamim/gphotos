@file:Suppress("unused")

package com.example.photoclone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface PickedImageDao {
    @Query("SELECT * FROM picked_images ORDER BY addedAt DESC")
    fun getAll(): Flow<List<PickedImage>>

    @Insert
    suspend fun insert(image: PickedImage): Long

    @Delete
    suspend fun delete(image: PickedImage)

    @Query("DELETE FROM picked_images WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM picked_images")
    suspend fun clearAll()
}
