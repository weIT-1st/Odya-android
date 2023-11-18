package com.weit.domain.usecase.placesearchhistory

import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import javax.inject.Inject

class DeleteAllRecentPlaceSearchUseCase @Inject constructor(
    private val placeSearchHistoryRepository: PlaceSearchHistoryRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        placeSearchHistoryRepository.setRecentPlaceSearch(emptyList())
}
