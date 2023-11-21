package com.weit.domain.repository.place

interface PlaceSearchHistoryRepository {

    suspend fun setRecentPlaceSearch(
        searchTermList: List<String>
    ): Result<Unit>

    suspend fun getRecentPlaceSearch(): Result<List<String>>

    suspend fun registerPlaceSearchHistory(
        searchTerm: String
    ): Result<Unit>

    suspend fun getPlaceSearchHistoryRank(): Result<List<String>>

    suspend fun getPlaceSearchHistoryAgeRank(
        ageRange: Int?
    ): Result<List<String>>
}
