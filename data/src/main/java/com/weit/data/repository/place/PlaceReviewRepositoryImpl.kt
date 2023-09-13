package com.weit.data.repository.place

import android.content.res.Resources.NotFoundException
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.source.PlaceReviewDateSource
import com.weit.data.util.exception
import com.weit.domain.model.exception.ForbiddenException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.repository.place.PlaceReviewRepository
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    override suspend fun getByPlaceId(info: PlaceReviewByPlaceIdQuery): Result<List<PlaceReviewDetail>> {
        return runCatching {
            dataSource.getByPlaceId(info).reviews.map {
                val placeReviewDetail = PlaceReviewDetail(
                    it.placeReviewId,
                    it.placeId,
                    it.userId,
                    it.writerNickname,
                    it.starRating,
                    it.review,
                    LocalDate.parse(it.createdAt.substring(0, 10)),
                )
                placeReviewDetail
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
                    LocalDate.parse(it.createdAt.substring(0, 10), DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                )
            }
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
            dataSource.getByPlaceId(PlaceReviewByPlaceIdQuery(placeId, 20))
        }
        return if (result.isSuccess) {
            Result.success(result.getOrThrow().averageRating)
        } else {
            Result.failure(result.exception())
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
            else -> UnKnownException()
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
}
