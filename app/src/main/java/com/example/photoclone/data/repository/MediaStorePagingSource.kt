package com.example.photoclone.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.photoclone.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PagingSource to page through MediaStore images and return Photo objects including size metadata.
 */
class MediaStorePagingSource(
    private val context: Context,
    private val pageSize: Int = 50
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val start = params.key ?: 0
        return try {
            val photos = withContext(Dispatchers.IO) {
                val list = mutableListOf<Photo>()
                val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                // Request width/height where available
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT
                )
                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
                context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                    if (cursor.count == 0) return@withContext list
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val widthCol = try { cursor.getColumnIndex(MediaStore.Images.Media.WIDTH) } catch (_: Exception) { -1 }
                    val heightCol = try { cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT) } catch (_: Exception) { -1 }
                    val moved = cursor.moveToPosition(start)
                    var collected = 0
                    if (moved) {
                        do {
                            val id = cursor.getLong(idCol)
                            val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            val w = if (widthCol >= 0) cursor.getInt(widthCol) else 0
                            val h = if (heightCol >= 0) cursor.getInt(heightCol) else 0
                            list += Photo(
                                id = id,
                                imageUrl = contentUri.toString(),
                                thumbnailUrl = contentUri.toString(),
                                width = w,
                                height = h
                            )
                            collected++
                        } while (collected < pageSize && cursor.moveToNext())
                    }
                }
                list
            }

            val nextKey = if (photos.size < pageSize) null else start + pageSize
            LoadResult.Page(data = photos, prevKey = if (start == 0) null else (start - pageSize).coerceAtLeast(0), nextKey = nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
    }
}
