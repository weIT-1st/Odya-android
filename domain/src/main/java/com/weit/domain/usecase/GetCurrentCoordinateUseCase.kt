package com.weit.domain.usecase

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.repository.CoordinateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentCoordinateUseCase @Inject constructor(
    private val repository: CoordinateRepository,
) {
    suspend operator fun invoke(): Result<Flow<CoordinateInfo>> =
        repository.getCurCoordinate()
}
