package com.weit.domain.usecase

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.repository.CoordinateRepository
import javax.inject.Inject

class InsertCoordinateUseCase @Inject constructor(
    private val repository: CoordinateRepository,
) {
    suspend operator fun invoke(lat: Float, lng: Float) =
        repository.insertCoordinate(CoordinateInfo(lat, lng))
}
