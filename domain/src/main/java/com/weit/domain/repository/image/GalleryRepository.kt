package com.weit.domain.repository.image

interface GalleryRepository {
    suspend fun pickImages(): List<String>
}
