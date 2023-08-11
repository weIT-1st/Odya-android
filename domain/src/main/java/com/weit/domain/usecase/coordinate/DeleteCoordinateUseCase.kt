package com.weit.domain.usecase.coordinate

import com.weit.domain.repository.coordinate.CoordinateRepository
import javax.inject.Inject

class DeleteCoordinateUseCase @Inject constructor(
    private val repository: CoordinateRepository,
) {
    suspend operator fun invoke(coordinateId: Long) =
        repository.deleteCoordinate(coordinateId)
}
