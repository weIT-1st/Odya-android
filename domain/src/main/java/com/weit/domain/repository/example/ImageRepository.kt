package com.weit.domain.repository.example

interface ImageRepository {
    suspend fun getImages(path: String?): Result<List<String>>

    suspend fun getImageBytes(uri: String): ByteArray
}
