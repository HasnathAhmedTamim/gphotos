package com.example.photoclone.data.repository

import android.content.ContentResolver
import android.provider.MediaStore
import com.example.photoclone.data.model.DeviceFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for retrieving device folders (Camera, Downloads, WhatsApp, etc.)
 */
class DeviceFoldersRepository(
    private val contentResolver: ContentResolver
) {

    /**
     * Get all device folders containing images
     */
    suspend fun getDeviceFolders(): List<DeviceFolder> = withContext(Dispatchers.IO) {
        val folders = mutableMapOf<String, FolderInfo>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn) ?: continue
                val bucketName = cursor.getString(bucketNameColumn) ?: continue
                val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    .buildUpon()
                    .appendPath(cursor.getLong(idColumn).toString())
                    .build()
                    .toString()
                val path = cursor.getString(dataColumn)

                if (folders.containsKey(bucketId)) {
                    folders[bucketId]?.let { info ->
                        folders[bucketId] = info.copy(count = info.count + 1)
                    }
                } else {
                    folders[bucketId] = FolderInfo(
                        id = bucketId,
                        name = bucketName,
                        path = path,
                        thumbnailUri = imageUri,
                        count = 1
                    )
                }
            }
        }

        folders.values.map { info ->
            DeviceFolder(
                id = info.id,
                name = info.name,
                path = info.path,
                itemCount = info.count,
                thumbnailUri = info.thumbnailUri
            )
        }.sortedByDescending { it.itemCount }
    }

    /**
     * Get photos from a specific device folder
     */
    suspend fun getPhotosInFolder(bucketId: String): List<String> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<String>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    .buildUpon()
                    .appendPath(cursor.getLong(idColumn).toString())
                    .build()
                    .toString()
                photos.add(imageUri)
            }
        }

        photos
    }

    private data class FolderInfo(
        val id: String,
        val name: String,
        val path: String,
        val thumbnailUri: String,
        val count: Int
    )
}
