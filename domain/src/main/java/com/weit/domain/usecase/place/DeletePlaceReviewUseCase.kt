package com.weit.domain.usecase.auth

import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class DeletePlaceReviewUseCase @Inject constructor(
    private val repository: PlaceReviewRepository,
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        repository.delete(id)
}
