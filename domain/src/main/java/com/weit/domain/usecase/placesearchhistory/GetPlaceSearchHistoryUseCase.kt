package com.weit.domain.usecase.placesearchhistory

import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import javax.inject.Inject

class GetPlaceSearchHistoryUseCase @Inject constructor(
    private val repository: PlaceSearchHistoryRepository
) {
    suspend operator fun invoke(): Result<Array<String>> =
        repository.getPlaceSearchHistoryRank()
}
