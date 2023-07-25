package com.weit.domain.usecase

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.repository.CoordinateRepository
import com.weit.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(): Result<Flow<CoordinateInfo>> =
        repository.getCurCoordinate()
}
