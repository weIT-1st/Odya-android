package com.weit.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
) {
    suspend fun getImages(path: String?): List<String> {
        return withContext(Dispatchers.IO) {
            val where = path?.takeIf { path.isNotEmpty() }?.apply {
                MediaStore.Images.Media.DATA + " LIKE '%$path%'"
            }
            val images = mutableListOf<String>()
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                where,
                null,
                MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC",
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val imageUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                    ).toString()
                    images.add(imageUri)
                }
            }
            images
        }
    }
}
