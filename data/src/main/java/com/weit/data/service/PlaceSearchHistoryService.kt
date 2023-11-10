package com.weit.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PlaceSearchHistoryService {

    @POST("/api/v1/place-search-histories")
    suspend fun registerPlaceSearchHistory(
        @Body searchTerm: String
    ): Response<Unit>

    @GET("/api/v1/place-search-histories/ranking")
    suspend fun getPlaceSearchHistoryRank(
    ): Array<String>

    @GET("/api/v1/place-search-histories/ranking/ageRange/2")
    suspend fun getPlaceSearchHistoryAgeRank(
        ageRange: Int?
    ): Array<String>
}
