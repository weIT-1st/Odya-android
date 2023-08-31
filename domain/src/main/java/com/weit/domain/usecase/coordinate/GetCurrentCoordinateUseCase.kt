package com.weit.domain.usecase.coordinate

import com.weit.domain.repository.coordinate.CoordinateRepository
import javax.inject.Inject

class GetCurrentCoordinateUseCase @Inject constructor(
    private val repository: CoordinateRepository,
) {
//    suspend operator fun invoke(): Result<Flow<CoordinateInfo>> =
//        repository.getCurCoordinate()
}
