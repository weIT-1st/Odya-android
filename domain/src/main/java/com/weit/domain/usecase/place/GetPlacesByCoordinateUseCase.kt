package com.weit.domain.usecase.place

import com.weit.domain.repository.place.PlaceRepository
import javax.inject.Inject

class GetPlacesByCoordinateUseCase @Inject constructor(
    private val repository: PlaceRepository,
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ) = repository.getPlacesByCoordinate(latitude, longitude)
}
