package com.weit.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
) {
    suspend fun getImages(path: String?): List<String> =
        withContext(Dispatchers.IO) {
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

    suspend fun getCompressedBytes(bitmap: Bitmap): ByteArray =
        withContext(Dispatchers.IO) {
            ByteArrayOutputStream().use { outputStream ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, DEFAULT_QUALITY, outputStream)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.WEBP, DEFAULT_QUALITY, outputStream)
                }
                outputStream.toByteArray()
            }
        }

    suspend fun getBitmapByUri(uri: String): Bitmap =
        withContext(Dispatchers.IO) {
            contentResolver.openInputStream(Uri.parse(uri)).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }

    suspend fun getLatLongByUri(uri: String?): List<Float> =
        withContext(Dispatchers.IO) {
            val coordinates = mutableListOf<Float>()
            contentResolver.openInputStream(Uri.parse(uri)).use { inputStream ->
                val exif = inputStream?.let { ExifInterface(it) }
                if (exif != null) {
                    val latLong = FloatArray(2)
                    if (exif.getLatLong(latLong)) {
                        coordinates.add(latLong[0])
                        coordinates.add(latLong[1])
                    }
                }
            }
            coordinates
        }

    suspend fun getScaledBitmap(bitmap: Bitmap): Bitmap =
        withContext(Dispatchers.IO) {
            val (width, height) = getScaledWidthAndHeight(bitmap.width, bitmap.height)
            Bitmap.createScaledBitmap(bitmap, width, height, false)
        }

    private fun getScaledWidthAndHeight(width: Int, height: Int) =
        when {
            width in DEFAULT_RESIZE until height -> DEFAULT_RESIZE to (DEFAULT_RESIZE / width.toFloat() * height).toInt()
            height in DEFAULT_RESIZE until width -> (DEFAULT_RESIZE / height.toFloat() * height).toInt() to DEFAULT_RESIZE
            else -> width to height
        }

    companion object {
        private const val DEFAULT_QUALITY = 90
        private const val DEFAULT_RESIZE = 1024
    }
}
