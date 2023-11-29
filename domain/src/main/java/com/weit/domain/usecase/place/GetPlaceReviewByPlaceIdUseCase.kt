package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewBySearching
import com.weit.domain.model.place.PlaceReviewContent
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class GetPlaceReviewByPlaceIdUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {

    suspend operator fun invoke(info: PlaceReviewByPlaceIdQuery): Result<List<PlaceReviewContent>> =
        repository.getByPlaceId(info)
}
