package com.weit.domain.usecase

import com.weit.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteLocationsUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(locationId: Long) =
        repository.deleteLocation(locationId)
}
