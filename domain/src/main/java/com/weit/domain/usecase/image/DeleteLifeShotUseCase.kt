package com.weit.domain.usecase.image

import com.weit.domain.repository.image.ImageAPIRepository
import javax.inject.Inject

class DeleteLifeShotUseCase @Inject constructor(
    private val repository: ImageAPIRepository
) {
    suspend operator fun invoke(imageId: Long): Result<Unit> =
        repository.deleteLifeShot(imageId)
}
