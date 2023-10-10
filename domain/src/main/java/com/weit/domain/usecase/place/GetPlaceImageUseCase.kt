package com.weit.domain.usecase.place

import com.weit.domain.repository.place.PlaceRepository
import javax.inject.Inject

class GetPlaceImageUseCase @Inject constructor(
    private val repository: PlaceRepository,
) {
    suspend operator fun invoke(placeId: String): Result<ByteArray?> =
        repository.getPlaceImage(placeId)
}
