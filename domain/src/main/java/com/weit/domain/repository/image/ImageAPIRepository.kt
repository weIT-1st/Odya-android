package com.weit.domain.repository.image

import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.domain.model.image.UserImageResponseInfo

interface ImageAPIRepository {

    suspend fun getUserImage(
        size: Int?,
        lastId: Long?
    ): Result<List<UserImageResponseInfo>>

    suspend fun setLifeShot(
        imageId: Long,
        placeName: String
    ): Result<Unit>

    suspend fun deleteLifeShot(imageId: Long): Result<Unit>

    suspend fun getCoordinateUserImage(
        leftLongitude: Double,
        bottomLatitude: Double,
        rightLongitude: Double,
        topLatitude: Double,
        size: Int?
    ): Result<List<CoordinateUserImageResponseInfo>>
}
