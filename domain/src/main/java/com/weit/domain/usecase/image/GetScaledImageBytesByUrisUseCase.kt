package com.weit.domain.usecase.image

import com.weit.domain.repository.image.ImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

class GetScaledImageBytesByUrisUseCase @Inject constructor(
    private val repository: ImageRepository,
) {
    suspend operator fun invoke(uris: List<String>): List<ByteArray> {
        val uriAndBytes = hashMapOf<String, ByteArray>()
        uris.map { uri ->
            CoroutineScope(Dispatchers.IO).async {
                uriAndBytes[uri] = repository.getImageBytes(uri)
            }
        }.awaitAll()
        return uris.mapNotNull { uriAndBytes[it] }
    }
}
