package com.weit.domain.usecase

import com.weit.domain.repository.CoordinateRepository
import javax.inject.Inject

class DeleteCoordinateUseCase @Inject constructor(
    private val repository: CoordinateRepository,
) {
    suspend operator fun invoke(coordinateId: Long) =
        repository.deleteCoordinate(coordinateId)
}
