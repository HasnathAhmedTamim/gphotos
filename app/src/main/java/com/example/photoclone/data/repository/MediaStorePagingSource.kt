package com.example.photoclone.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * PagingSource to page through MediaStore images. Each key is an offset-based Int page key.
 */
class MediaStorePagingSource(
    private val context: Context,
    private val pageSize: Int = 50
) : PagingSource<Int, Uri>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        val start = params.key ?: 0
        val uris = mutableListOf<Uri>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        try {
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                // Move to the start offset and collect up to pageSize items
                if (cursor.count == 0) {
                    return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                }
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val moved = cursor.moveToPosition(start)
                var collected = 0
                if (moved) {
                    do {
                        val id = cursor.getLong(idCol)
                        uris += ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        collected++
                    } while (collected < pageSize && cursor.moveToNext())
                } else {
                    // If moveToPosition failed (e.g., start >= count), return empty page
                }
            }
            val nextKey = if (uris.size < pageSize) null else start + pageSize
            return LoadResult.Page(data = uris, prevKey = if (start == 0) null else (start - pageSize).coerceAtLeast(0), nextKey = nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
    }
}
