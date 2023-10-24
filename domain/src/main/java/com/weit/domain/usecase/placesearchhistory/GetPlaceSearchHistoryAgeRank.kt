package com.weit.domain.usecase.placesearchhistory

import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import javax.inject.Inject

class GetPlaceSearchHistoryAgeRank @Inject constructor(
    private val repository: PlaceSearchHistoryRepository
) {
    suspend operator fun invoke(ageRange: Int?): Result<Array<String>> =
        repository.getPlaceSearchHistoryAgeRank(ageRange)
}
