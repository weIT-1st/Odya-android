package com.weit.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.weit.data.repository.image.ImageRepositoryImpl
import com.weit.domain.model.exception.ImageNotFoundException
import com.weit.domain.model.image.ImageLatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
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

    suspend fun getLatLongByUri(uri: String?): ImageLatLng? =
        withContext(Dispatchers.IO) {
            var imageLatLng = FloatArray(2)
            contentResolver.openInputStream(Uri.parse(uri))?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                if (exif != null) {
                    val latLong = FloatArray(2)
                    if (exif.getLatLong(latLong)) {
                        imageLatLng[0] = latLong[0]
                        imageLatLng[1] = latLong[1]
                    }
                }
            }
            ImageLatLng(imageLatLng[0], imageLatLng[1])
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

    suspend fun getImageName(uri: String): String =
        withContext(Dispatchers.IO) {
            val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
            val cursor = contentResolver.query(Uri.parse(uri), projection, null, null, null)
            var imageName: String? = null
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameColumnIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                    imageName = File(it.getString(nameColumnIndex)).nameWithoutExtension
                }
            }
            imageName ?: throw ImageNotFoundException()
        }

    suspend fun getRotate(uri: String): Float {
        var orientation : Float = 0f
        contentResolver.openInputStream(Uri.parse(uri))?.use { inputStream ->
            val exif = ExifInterface(inputStream)
            orientation = when (exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 0)) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        }
        return orientation
    }

    suspend fun getImageByte(uri: String): ByteArray {
        val bitmap = getBitmapByUri(uri)
        val rotateBitmap = bitmap.getRotatedBitmap(getRotate(uri))
        val scaleBitmap = getScaledBitmap(rotateBitmap)
        val bytes = getCompressedBytes(scaleBitmap)
        scaleBitmap.recycle()
        return bytes
    }

    private fun Bitmap.getRotatedBitmap(degree: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degree) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true) ?: this
    }

    suspend fun getRequestBody(uri: String): RequestBody{
        val bytes = getImageByte(uri)
        return bytes.toRequestBody("image/webp".toMediaType(), 0, bytes.size)
    }



    companion object {
        private const val DEFAULT_QUALITY = 90
        private const val DEFAULT_RESIZE = 1024
    }
}
