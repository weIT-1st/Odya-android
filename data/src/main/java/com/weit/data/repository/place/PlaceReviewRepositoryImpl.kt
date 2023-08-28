package com.weit.data.repository.place

import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.source.PlaceReviewDateSource
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import okhttp3.internal.http.HTTP_ALREADY_REPORTED
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.Response
import javax.inject.Inject

class PlaceReviewRepositoryImpl @Inject constructor(
    private val dataSource: PlaceReviewDateSource,
) : PlaceReviewRepository {

    override suspend fun register(info: PlaceReviewRegistrationInfo): Result<Unit> {
        val response = dataSource.register(info.toPlaceReviewRegistraion())
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleReviewError(response))
        }
    }

    override suspend fun update(info: PlaceReviewUpdateInfo): Result<Unit> {
        val response = dataSource.update(info.toPlaceReviewModification())
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleReviewError(response))
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
                )
            }
        }
    }

    override suspend fun isExistReview(placeId: String): Result<Boolean> {
        val response = dataSource.isExistReview(placeId)

        return if (response.isSuccessful) {
            Result.success(response.body()!!.isExist)
        } else {
            Result.failure(handleReviewError(response))
        }
    }

    override suspend fun getReviewCount(placeId: String): Result<Int> {
        val response = dataSource.getReviewCount(placeId)

        return if (response.isSuccessful) {
            Result.success(response.body()!!.count)
        } else {
            Result.failure(handleReviewError(response))
        }
    }

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

    private fun handleReviewError(response: Response<*>): Throwable {
        return when (response.code()) {
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_ALREADY_REPORTED -> DuplicatedSomethingException()
//            HTTP_FORBIDDEN -> NotHavePermissionException()
            else -> UnKnownException()
        }
    }
}
