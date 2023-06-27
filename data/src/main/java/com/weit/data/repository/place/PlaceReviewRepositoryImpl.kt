package com.weit.data.repository.place

import com.weit.data.source.PlaceReviewDateSource
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
            dataSource.register(info)
        }
    }

    override suspend fun update(info: PlaceReviewUpdateInfo): Result<Unit> {
        return runCatching {
            dataSource.update(info)
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
}
