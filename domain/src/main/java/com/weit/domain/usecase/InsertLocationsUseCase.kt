package com.weit.domain.usecase

import com.weit.domain.model.LocationInfo
import com.weit.domain.repository.LocationRepository
import javax.inject.Inject

class InsertLocationsUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(time: Long, lat:Float, lng:Float) =
        repository.insertLocation(LocationInfo(time,lat,lng))
}
