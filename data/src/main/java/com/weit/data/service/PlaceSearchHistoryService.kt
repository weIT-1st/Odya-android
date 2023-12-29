package com.weit.data.service

import com.weit.data.model.place.SearchTermDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PlaceSearchHistoryService {

    @POST("/api/v1/place-search-histories")
    suspend fun registerPlaceSearchHistory(
        @Body searchTerm: SearchTermDTO
    ): Response<Unit>

    @GET("/api/v1/place-search-histories/ranking")
    suspend fun getPlaceSearchHistoryRank(
    ): List<String>

    @GET("/api/v1/place-search-histories/ranking/ageRange/2")
    suspend fun getPlaceSearchHistoryAgeRank(
        ageRange: Int?
    ): List<String>
}
