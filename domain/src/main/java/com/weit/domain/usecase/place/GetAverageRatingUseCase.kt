package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class GetAverageRatingUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(placeId: String): Result<Float> =
        repository.getAverageRating(placeId)

}
