package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.image.CoordinateUserImageResponseDTO
import com.weit.data.model.image.PlaceNameDTO
import com.weit.data.model.image.UserImageResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ImageAPIService {

    @GET("/api/v1/images?")
    suspend fun getUserImage(
        @Query("size") size: Int? = 10,
        @Query("lastId") lastId: Long?
    ): ListResponse<UserImageResponseDTO>

    @POST("/api/v1/images/{imageId}/life-shot")
    suspend fun setLifeShot(
        @Path("imageId") imageId: Long,
        @Body placeName: PlaceNameDTO
    ): Response<Unit>

    @DELETE("/api/v1/images/{imageId}/life-shot")
    suspend fun deleteLifeShot(
        @Path("imageId") imageId: Long
    ): Response<Unit>

    @GET("/api/v1/images/coordinate?")
    suspend fun getCoordinateUserImage(
        @Query("leftLongitude") leftLongitude: Double,
        @Query("bottomLatitude") bottomLatitude: Double,
        @Query("rightLongitude") rightLongitude: Double,
        @Query("topLatitude") topLatitude: Double,
        @Query("size") size: Int? = 10,
    ): List<CoordinateUserImageResponseDTO>
}
