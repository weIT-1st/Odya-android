package com.weit.data.repository.place

import android.content.res.Resources.NotFoundException
import com.weit.data.model.place.PlaceReviewDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.source.PlaceReviewDateSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.ForbiddenException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.RequestResourceAlreadyExistsException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewBySearching
import com.weit.domain.model.place.PlaceReviewByUserIdQuery
import com.weit.domain.model.place.PlaceReviewContent
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.model.place.UserInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class PlaceReviewRepositoryImpl @Inject constructor(
    private val dataSource: PlaceReviewDateSource,
) : PlaceReviewRepository {

    private val hasNextReviewByPlaceID = AtomicBoolean(true)
    private val hasNextReviewByUserID = AtomicBoolean(true)

    override suspend fun register(info: PlaceReviewRegistrationInfo): Result<Unit> {
        val response = dataSource.register(info.toPlaceReviewRegistraion())
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleReviewError(handleResponseError(response)))
        }
    }

    override suspend fun update(info: PlaceReviewUpdateInfo): Result<Unit> {
        val response = dataSource.update(info.toPlaceReviewModification())
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleReviewError(handleResponseError(response)))
        }
    }

    override suspend fun delete(placeReviewId: Long): Result<Unit> {
        val response = dataSource.delete(placeReviewId)

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(handleReviewError(handleResponseError(response)))
        }
    }

    override suspend fun getByPlaceId(info: PlaceReviewByPlaceIdQuery): Result<PlaceReviewBySearching> {
        if (info.lastPlaceReviewId == null) {
            hasNextReviewByPlaceID.set(true)
        }

        val result = kotlin.runCatching { dataSource.getByPlaceId(info) }

        if (hasNextReviewByPlaceID.get().not()) {
            return Result.failure(NoMoreItemException())
        }

        return if (result.isSuccess) {
            val placeReview = result.getOrThrow()
            hasNextReviewByPlaceID.set(placeReview.hasNext)
            Result.success(placeReview.toPlaceReviewBySearching())
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    override suspend fun getByUserId(info: PlaceReviewByUserIdQuery): Result<PlaceReviewBySearching> {
        if (info.lastPlaceReviewId == null) {
            hasNextReviewByUserID.set(true)
        }

        if (hasNextReviewByUserID.get().not()) {
            return Result.failure(NoMoreItemException())
        }
        val result = runCatching { dataSource.getByUserId(info) }
        return if (result.isSuccess) {
            val placeReview = result.getOrThrow()
            hasNextReviewByPlaceID.set(placeReview.hasNext)
            Result.success(placeReview.toPlaceReviewBySearching())
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    override suspend fun isExistReview(placeId: String): Result<Boolean> {
        val result = runCatching {
            dataSource.isExistReview(placeId)
        }
        return if (result.isSuccess) {
            val isExist = result.getOrThrow().isExist
            Result.success(isExist)
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    override suspend fun getReviewCount(placeId: String): Result<Int> {
        val result = runCatching {
            dataSource.getReviewCount(placeId)
        }
        return if (result.isSuccess) {
            val count = result.getOrThrow().count
            Result.success(count)
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    override suspend fun getAverageRating(placeId: String): Result<Float> {
        val result = runCatching {
            dataSource.getByPlaceId(PlaceReviewByPlaceIdQuery(placeId))
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow().averageRating)
        } else {
            Result.failure(handleReviewError(result.exception()))
        }
    }

    private fun handleReviewError(t: Throwable): Throwable {
        return if (t is HttpException) {
            handleCode(t.code())
        } else {
            t
        }
    }

    private fun handleCode(code: Int): Throwable {
        return when (code) {
            HTTP_BAD_REQUEST -> InvalidRequestException()
            HTTP_UNAUTHORIZED -> InvalidTokenException()
            HTTP_NOT_FOUND -> NotFoundException()
            HTTP_FORBIDDEN -> ForbiddenException()
            HTTP_CONFLICT -> RequestResourceAlreadyExistsException()
            else -> UnKnownException()
        }
    }

    private fun handleResponseError(response: Response<*>): Throwable {
        return handleCode(response.code())
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

    private fun PlaceReviewDTO.toPlaceReviewBySearching(): PlaceReviewBySearching =
        PlaceReviewBySearching(
            hasNext = hasNext,
            averageRating = averageRating,
            content = reviews.map {
                PlaceReviewContent(
                    it.placeReviewId,
                    it.placeId,
                    UserInfo(
                        it.userInfo.userId,
                        it.userInfo.nickname,
                        it.userInfo.profile,
                    ),
                    it.starRating,
                    it.review,
                    LocalDateTime.parse(it.createdAt, DateTimeFormatter.ISO_DATE_TIME),
                )
            },
        )
}
