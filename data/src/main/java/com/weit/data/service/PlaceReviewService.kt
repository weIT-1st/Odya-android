package com.weit.data.service

import com.weit.data.model.place.PlaceReviewListDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceReviewService {

    @POST("/api/v1/place-reviews")
    suspend fun register(
        @Body placeReviewRegistration: PlaceReviewRegistration,
    )

    @PATCH("/api/v1/place-reviews")
    suspend fun update(
        @Body placeReviewModification: PlaceReviewModification,
    )

    @DELETE("/api/v1/place-reviews/{id}")
    suspend fun delete(
        @Path("id") id: Long,
    )

    @GET("/api/v1/place-reviews/places/{id}")
    suspend fun getReviewsByPlaceId(
        @Path("id") placeId: String,
        @Query("size") size: Int,
        @Query("sortType") sortType: String?,
        @Query("lastId") lastPlaceReviewId: Long?,
    ): PlaceReviewListDTO

    @GET("/api/v1/place-reviews/users/{id}")
    suspend fun getReviewsByUserId(
        @Path("id") userId: Long,
        @Query("size") size: Int,
        @Query("sortType") sortType: String?,
        @Query("lastId") lastPlaceReviewId: Long?,
    ): PlaceReviewListDTO
}
