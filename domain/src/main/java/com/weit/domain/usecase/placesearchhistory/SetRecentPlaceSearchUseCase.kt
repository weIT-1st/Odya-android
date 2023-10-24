package com.weit.domain.usecase.placesearchhistory

import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import javax.inject.Inject

class SetRecentPlaceSearchUseCase @Inject constructor(
    private val placeSearchHistoryRepository: PlaceSearchHistoryRepository
) {
    suspend operator fun invoke(searchTermList: Set<String>): Result<Unit> =
        placeSearchHistoryRepository.setRecentPlaceSearch(searchTermList)
}
