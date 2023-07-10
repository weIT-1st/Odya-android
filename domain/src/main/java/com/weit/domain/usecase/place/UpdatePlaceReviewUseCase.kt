package com.weit.domain.usecase.place

import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class UpdatePlaceReviewUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(info: PlaceReviewUpdateInfo): Result<Unit> =
        repository.update(info)
}
