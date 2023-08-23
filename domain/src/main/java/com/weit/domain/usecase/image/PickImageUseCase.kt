package com.weit.domain.usecase.image

import com.weit.domain.repository.image.GalleryRepository
import javax.inject.Inject

class PickImageUseCase @Inject constructor(
    private val repository: GalleryRepository,
) {
    suspend operator fun invoke(): List<String> {
        return repository.pickImages()
    }
}
