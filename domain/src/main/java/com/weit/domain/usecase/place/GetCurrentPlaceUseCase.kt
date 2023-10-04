package com.weit.domain.usecase.place

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.repository.place.PlaceRepository
import javax.inject.Inject

class GetCurrentPlaceUseCase @Inject constructor(
    private val repository: PlaceRepository
) {
    suspend operator fun invoke(): Result<CoordinateInfo> =
        repository.getCurrentPlaceDetail()
}
