package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewBySearching
import com.weit.domain.model.place.PlaceReviewByUserIdQuery
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class GetPlaceReviewByUserIdUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(info: PlaceReviewByUserIdQuery): Result<PlaceReviewBySearching> =
        repository.getByUserId(info)
}
