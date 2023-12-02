package com.weit.data.source

import com.weit.data.model.ListResponse
import com.weit.data.model.image.CoordinateUserImageResponseDTO
import com.weit.data.model.image.PlaceNameDTO
import com.weit.data.model.image.UserImageResponseDTO
import com.weit.data.service.ImageAPIService
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class ImageAPIDataSource @Inject constructor(
    private val service: ImageAPIService
) {

    suspend fun getUserImage(
        size: Int?,
        lastId: Long?
    ): ListResponse<UserImageResponseDTO> =
        service.getUserImage(size, lastId)

    suspend fun setLifeShot(
        imageId: Long,
        placeName: PlaceNameDTO
    ): Response<Unit> =
        service.setLifeShot(imageId, placeName)

    suspend fun deleteLifeShot(imageId: Long): Response<Unit> =
        service.deleteLifeShot(imageId)

    suspend fun getCoordinateUserImage(
        leftLongitude: Double,
        bottomLatitude: Double,
        rightLongitude: Double,
        topLatitude: Double,
        size: Int?
    ): List<CoordinateUserImageResponseDTO> =
        service.getCoordinateUserImage(
            leftLongitude, bottomLatitude, rightLongitude, topLatitude, size)
}
