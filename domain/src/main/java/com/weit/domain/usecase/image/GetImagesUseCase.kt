package com.weit.domain.usecase.image

import com.weit.domain.repository.example.ImageRepository
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(
    private val repository: ImageRepository,
) {
    suspend operator fun invoke(path: String? = null): Result<List<String>> =
        repository.getImages(path)
}
