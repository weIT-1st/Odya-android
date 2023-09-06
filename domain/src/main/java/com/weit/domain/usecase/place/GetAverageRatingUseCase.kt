package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class GetAverageRatingUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(placeId: String): Result<Int> =
        repository.getAverageRating(PlaceReviewByPlaceIdInfo(placeId, 20))
}
