package com.weit.domain.repository.place

interface PlaceSearchHistoryRepository {

    suspend fun setRecentPlaceSearch(
        searchTermList: Set<String>
    ): Result<Unit>

    suspend fun getRecentPlaceSearch(): Result<Set<String>?>

    suspend fun registerPlaceSearchHistory(
        searchTerm: String
    ): Result<Unit>

    suspend fun getPlaceSearchHistoryRank(): Result<Array<String>>

    suspend fun getPlaceSearchHistoryAgeRank(
        ageRange: Int?
    ): Result<Array<String>>
}
