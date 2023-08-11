package com.weit.domain.usecase.coordinate

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import javax.inject.Inject

class GetStoredCoordinatesUseCase @Inject constructor(
    private val repository: CoordinateRepository,
) {
    suspend operator fun invoke(startTime: Long, endTime: Long): List<CoordinateInfo> =
        repository.getCoordinatesInfo(CoordinateTimeInfo(startTime, endTime))
}
