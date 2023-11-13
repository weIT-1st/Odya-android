package com.weit.domain.usecase.image

import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.domain.model.image.ImageDoubleLatLng
import com.weit.domain.repository.image.ImageAPIRepository
import javax.inject.Inject

class GetCoordinateUserImageUseCase @Inject constructor(
    private val repository: ImageAPIRepository
) {
    suspend operator fun invoke(
        rightUp: ImageDoubleLatLng,
        leftDown: ImageDoubleLatLng
    ): Result<List<CoordinateUserImageResponseInfo>> =
        repository.getCoordinateUserImage(
            leftDown.longitude,
            leftDown.latitude,
            rightUp.longitude,
            rightUp.latitude,
            null)
}

