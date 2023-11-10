package com.weit.domain.usecase.image

import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.domain.repository.image.ImageAPIRepository
import javax.inject.Inject

class GetCoordinateUserImageUseCase @Inject constructor(
    private val repository: ImageAPIRepository
) {
    suspend operator fun invoke(leftLongitude: Double,
                                bottomLatitude: Double,
                                rightLongitude: Double,
                                topLatitude: Double,
                                size: Int?
    ): Result<List<CoordinateUserImageResponseInfo>> =
        repository.getCoordinateUserImage(leftLongitude, bottomLatitude, rightLongitude, topLatitude, size)
}
