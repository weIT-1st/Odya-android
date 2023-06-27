package com.weit.domain.repository.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewInfo
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

    suspend fun getByPlaceId(
        info: PlaceReviewByPlaceIdInfo,
    ): Result<List<PlaceReviewInfo>>

    suspend fun getByUserId(
        info: PlaceReviewByUserIdInfo,
    ): Result<List<PlaceReviewInfo>>
}
