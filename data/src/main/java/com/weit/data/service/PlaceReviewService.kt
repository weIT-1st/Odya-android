package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.place.AverageRatingDTO
import com.weit.data.model.place.IsExistPlaceReviewDTO
import com.weit.data.model.place.PlaceReviewContentDTO
import com.weit.data.model.place.PlaceReviewCountDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import retrofit2.Response
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
    ): Response<Unit>

    @PATCH("/api/v1/place-reviews")
    suspend fun update(
        @Body placeReviewModification: PlaceReviewModification,
    ): Response<Unit>

    @DELETE("/api/v1/place-reviews/{id}")
    suspend fun delete(
        @Path("id") placeReviewId: Long,
    ): Response<Unit>

    @GET("/api/v1/place-reviews/places/{id}")
    suspend fun getReviewsByPlaceId(
        @Path("id") placeId: String,
        @Query("size") size: Int,
        @Query("sortType") sortType: String?,
        @Query("lastId") lastPlaceReviewId: Long?,
    ): ListResponse<PlaceReviewContentDTO>

    @GET("/api/v1/place-reviews/users/{id}")
    suspend fun getReviewsByUserId(
        @Path("id") userId: Long,
        @Query("size") size: Int,
        @Query("sortType") sortType: String?,
        @Query("lastId") lastPlaceReviewId: Long?,
    ): ListResponse<PlaceReviewContentDTO>

    @GET("/api/v1/place-reviews/{id}")
    suspend fun isExistReview(
        @Path("id") placeId: String,
    ): IsExistPlaceReviewDTO

    @GET("/api/v1/place-reviews/count/{id}")
    suspend fun getReviewCount(
        @Path("id") placeId: String,
    ): PlaceReviewCountDTO

    @GET("/api/v1/place-reviews/average/{id}")
    suspend fun getAverageRating(
        @Path("id") placeId: String
    ): AverageRatingDTO
}
