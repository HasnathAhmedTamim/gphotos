@file:Suppress("unused")

package com.example.photoclone.data.repository

import android.content.Context
import com.example.photoclone.data.local.AppDatabase
import com.example.photoclone.data.local.PickedImage
import kotlinx.coroutines.flow.Flow

class PickedImagesRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).pickedImageDao()

    fun getPickedImages(): Flow<List<PickedImage>> = dao.getAll()

    suspend fun savePickedUri(uri: String): Long {
        return dao.insert(PickedImage(uri = uri))
    }

    suspend fun deletePicked(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
