package com.weit.data.service

import com.weit.data.model.place.PlaceReviewListDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceReviewService {

    @POST("/api/v1/place-reviews")
    @FormUrlEncoded
    suspend fun register(
      @Body placeReviewRegistration: PlaceReviewRegistration
    )

    @PATCH("/api/v1/place-reviews")
    suspend fun update(
       @Body placeReviewModification: PlaceReviewModification
    )

    @DELETE("/api/v1/place-reviews/{id}")
    suspend fun delete(
        @Path("id") id: Long,
    )

    @GET("/api/v1/place-reviews/place/{id}")
    suspend fun getReviewsByPlaceId(
        @Path("id") id: String,
        @Query("startId") startId: Long?,
        @Query("count") count: Int,
    ): PlaceReviewListDTO

    @GET("/api/v1/place-reviews/user/{id}")
    suspend fun getReviewsByUserId(
        @Path("id") id: Long,
        @Query("startId") startId: Long?,
        @Query("count") count: Int,
    ): PlaceReviewListDTO
}
