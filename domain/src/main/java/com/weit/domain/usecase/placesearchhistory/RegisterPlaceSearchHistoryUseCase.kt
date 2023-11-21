package com.weit.domain.usecase.placesearchhistory

import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import javax.inject.Inject

class RegisterPlaceSearchHistoryUseCase @Inject constructor(
    private val repository: PlaceSearchHistoryRepository
) {
    suspend operator fun invoke(searchTerm : String): Result<Unit> =
        repository.registerPlaceSearchHistory(searchTerm)
}
