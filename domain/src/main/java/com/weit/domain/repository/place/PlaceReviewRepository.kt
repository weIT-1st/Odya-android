package com.weit.domain.repository.place

import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo

interface PlaceReviewRepository {
    suspend fun register(
        info: PlaceReviewRegistrationInfo,
    ): Result<Unit>

    suspend fun update(
        info: PlaceReviewUpdateInfo,
    ): Result<Unit>

    suspend fun delete(
        id: Long,
    ): Result<Unit>
}
