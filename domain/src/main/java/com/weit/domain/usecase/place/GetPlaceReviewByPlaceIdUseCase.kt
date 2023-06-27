package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class GetPlaceReviewByPlaceIdUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(info: PlaceReviewByPlaceIdInfo): Result<List<PlaceReviewDetail>> =
        repository.getByPlaceId(info)
}
