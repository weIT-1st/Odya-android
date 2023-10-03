package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.repository.place.PlaceRepository
import javax.inject.Inject

class GetPlaceDetailUseCase @Inject constructor(
    private val repository: PlaceRepository,
) {
    suspend operator fun invoke(placeId: String): PlaceDetail {
        return repository.getPlaceDetail(placeId).getOrThrow()
    }
}
