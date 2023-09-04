package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.repository.place.PlaceRepository
import javax.inject.Inject

class GetSearchPlaceUseCase @Inject constructor(
    private val repository: PlaceRepository,
) {
    suspend operator fun invoke(query: String): List<PlacePrediction> {
        return if (query.isBlank()) {
            emptyList()
        } else {
            repository.searchPlace(query)
        }
    }
}
