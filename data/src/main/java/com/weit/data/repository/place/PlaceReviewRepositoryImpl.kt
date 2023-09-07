package com.weit.data.repository.place

import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.source.PlaceReviewDateSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.NotExistPlaceReviewException
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import javax.inject.Inject

class PlaceReviewRepositoryImpl @Inject constructor(
    private val dataSource: PlaceReviewDateSource,
) : PlaceReviewRepository {

    override suspend fun register(info: PlaceReviewRegistrationInfo): Result<Unit> {
        return runCatching {
            dataSource.register(info.toPlaceReviewRegistraion())
        }
    }

    override suspend fun update(info: PlaceReviewUpdateInfo): Result<Unit> {
        return runCatching {
            dataSource.update(info.toPlaceReviewModification())
        }
    }

    override suspend fun delete(placeReviewId: Long): Result<Unit> {
        return runCatching {
            dataSource.delete(placeReviewId)
        }
    }

    override suspend fun getByPlaceId(info: PlaceReviewByPlaceIdInfo): Result<List<PlaceReviewDetail>> {
        return runCatching {
            dataSource.getByPlaceId(info).reviews.map {
                PlaceReviewDetail(
                    it.placeReviewId,
                    it.placeId,
                    it.userId,
                    it.writerNickname,
                    it.starRating,
                    it.review,
                    it.createdAt
                )
            }
        }
    }

    override suspend fun getByUserId(info: PlaceReviewByUserIdInfo): Result<List<PlaceReviewDetail>> {
        return runCatching {
            dataSource.getByUserId(info).reviews.map {
                PlaceReviewDetail(
                    it.placeReviewId,
                    it.placeId,
                    it.userId,
                    it.writerNickname,
                    it.starRating,
                    it.review,
                    it.createdAt
                )
            }
        }
    }

    override suspend fun isExistReview(placeId: String): Result<Boolean> {
        val result = runCatching {
            dataSource.isExistReview(placeId)
        }
        return if (result.isSuccess) {
            val isExist = result.getOrNull()?.isExist
            if (isExist == true) {
                Result.success(isExist)
            } else {
                Result.failure(NotExistPlaceReviewException())
            }
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getReviewCount(placeId: String): Result<Int> {
        val result = runCatching {
            dataSource.getReviewCount(placeId)
        }
        return if (result.isSuccess) {
            val count = result.getOrNull()?.count
            if (count != null) {
                Result.success(count)
            } else {
                Result.failure(result.exception())
            }
        } else {
            Result.failure(result.exception())
        }
    }

    override suspend fun getAverageRating(info: PlaceReviewByPlaceIdInfo): Result<Float> =
        runCatching { dataSource.getByPlaceId(info).averageRating }

    private fun PlaceReviewRegistrationInfo.toPlaceReviewRegistraion(): PlaceReviewRegistration =
        PlaceReviewRegistration(
            placeId = placeId,
            rating = rating,
            review = review,
        )

    private fun PlaceReviewUpdateInfo.toPlaceReviewModification(): PlaceReviewModification =
        PlaceReviewModification(
            placeReviewId = placeReviewId,
            rating = rating,
            review = review,
        )
}
