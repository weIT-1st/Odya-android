package com.weit.domain.usecase.auth

import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class UpdatePlaceReviewUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(info: PlaceReviewUpdateInfo): Result<Unit> =
        repository.update(info)
}
