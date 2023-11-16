package com.weit.domain.usecase.image

import com.weit.domain.repository.image.ImageAPIRepository
import javax.inject.Inject

class SetLifeShotUseCase @Inject constructor(
    private val repository: ImageAPIRepository
) {
    suspend operator fun invoke(imageId: Long, placeName: String): Result<Unit> =
        repository.setLifeShot(imageId, placeName)
}
