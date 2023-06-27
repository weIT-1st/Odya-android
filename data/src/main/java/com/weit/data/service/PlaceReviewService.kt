package com.weit.data.service

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlaceReviewService {

    @POST("/api/v1/place-reviews")
    @FormUrlEncoded
    suspend fun register(
        @Field("placeId") placeId: String,
        @Field("rating") rating: Int,
        @Field("review") review: String,
    )

    @PUT("/api/v1/place-reviews")
    @FormUrlEncoded
    suspend fun update(
        @Field("id") id: Long,
        @Field("rating") rating: Int,
        @Field("review") review: String,
    )

    @DELETE("/api/v1/place-reviews/{id}")
    suspend fun delete(
        @Path("id") id: Long
    )

}
