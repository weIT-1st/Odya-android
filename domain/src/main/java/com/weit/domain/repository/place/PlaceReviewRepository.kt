package com.weit.domain.repository.place

import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
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
        placeReviewId: Long,
    ): Result<Unit>

    suspend fun getByPlaceId(
        info: PlaceReviewByPlaceIdQuery,
    ): Result<List<PlaceReviewDetail>>

    suspend fun getByUserId(
        info: PlaceReviewByUserIdInfo,
    ): Result<List<PlaceReviewDetail>>

    suspend fun isExistReview(
        placeId: String,
    ): Result<Boolean>

    suspend fun getReviewCount(
        placeId: String,
    ): Result<Int>

    suspend fun getAverageRating(
        placeId: String
    ): Result<Float>
}
