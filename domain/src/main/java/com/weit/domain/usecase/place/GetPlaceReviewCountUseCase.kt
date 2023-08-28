package com.weit.domain.usecase.place

import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class GetPlaceReviewCountUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(placeId: String): Result<Int> =
        repository.getReviewCount(placeId)
}
