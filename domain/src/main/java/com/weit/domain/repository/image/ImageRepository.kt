package com.weit.domain.repository.image

interface ImageRepository {
    suspend fun getImages(path: String?): Result<List<String>>

    suspend fun getImageBytes(uri: String): ByteArray
}
