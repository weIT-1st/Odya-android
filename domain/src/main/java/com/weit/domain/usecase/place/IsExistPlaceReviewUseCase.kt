package com.weit.domain.usecase.place

import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class IsExistPlaceReviewUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(placeId: String): Result<Boolean> =
        repository.isExistReview(placeId)
}
