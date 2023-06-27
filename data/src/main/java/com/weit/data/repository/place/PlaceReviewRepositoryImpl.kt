package com.weit.data.repository.place

import com.weit.data.model.place.PlaceReviewDTO
import com.weit.data.model.place.PlaceReviewListDTO
import com.weit.data.source.PlaceReviewDateSource
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewInfo
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

    override suspend fun delete(id: Long): Result<Unit> {
        return runCatching {
            dataSource.delete(id)
        }
    }

    override suspend fun getByPlaceId(info: PlaceReviewByPlaceIdInfo): Result<List<PlaceReviewInfo>> {
        return runCatching {
            dataSource.getByPlaceId(info).reviews.map{
                PlaceReviewInfo(
                    it.id,
                    it.placeId,
                    it.userId,
                    it.writerNickname,
                    it.starRating,
                    it.review
                )
            }
        }
    }

    override suspend fun getByUserId(info: PlaceReviewByUserIdInfo): Result<List<PlaceReviewInfo>> {
        return runCatching {
            dataSource.getByUserId(info).reviews.map{
                PlaceReviewInfo(
                    it.id,
                    it.placeId,
                    it.userId,
                    it.writerNickname,
                    it.starRating,
                    it.review
                )
            }
        }
    }


}
