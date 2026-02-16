package com.example.photoclone.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.photoclone.data.model.Photo

/**
 * Small repository to load image Uris from the device gallery using MediaStore.
 */
object GalleryRepository {
    suspend fun loadGalleryImageUris(context: Context, limit: Int = 200): List<Uri> = withContext(Dispatchers.IO) {
        val uris = mutableListOf<Uri>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        // Sort by newest first (avoid using LIMIT in the sortOrder)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val resolver = context.contentResolver
        resolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count = 0
            while (cursor.moveToNext() && count < limit) {
                val id = cursor.getLong(idColumn)
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                uris += contentUri
                count++
            }
        }
        uris
    }

    // Return a flow of PagingData<Photo> so callers get size info for better layout decisions
    fun pagerForImages(context: Context, pageSize: Int = 50): Flow<PagingData<Photo>> {
        return Pager(PagingConfig(pageSize = pageSize, prefetchDistance = 2, enablePlaceholders = false)) {
            MediaStorePagingSource(context, pageSize)
        }.flow
    }
}
