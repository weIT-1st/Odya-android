package com.weit.domain.repository.image

import com.weit.domain.model.image.ImageLatLng

interface ImageRepository {
    suspend fun getImages(path: String?): Result<List<String>>

    suspend fun getImageBytes(uri: String): ByteArray

    suspend fun getCoordinates(uri: String?): ImageLatLng?
}
