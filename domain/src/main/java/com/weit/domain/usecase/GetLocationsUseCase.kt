package com.weit.domain.usecase

import com.weit.domain.model.LocationInfo
import com.weit.domain.model.LocationTimeInfo
import com.weit.domain.repository.LocationRepository
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(startTime: Long, endTime: Long): List<LocationInfo> =
        repository.getLocationsInfo(LocationTimeInfo(startTime, endTime))
}
