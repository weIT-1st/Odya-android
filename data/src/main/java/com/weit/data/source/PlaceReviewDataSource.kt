package com.weit.data.source

import com.weit.data.model.place.IsExistPlaceReviewDTO
import com.weit.data.model.place.PlaceReviewCountDTO
import com.weit.data.model.place.PlaceReviewDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.service.PlaceReviewService
import com.weit.domain.model.place.PlaceReviewByPlaceIdQuery
import com.weit.domain.model.place.PlaceReviewByUserIdQuery
import retrofit2.Response
import javax.inject.Inject

class PlaceReviewDataSource @Inject constructor(
    private val service: PlaceReviewService,
) {

    suspend fun register(placeReviewRegistration: PlaceReviewRegistration): Response<Unit> {
        return service.register(placeReviewRegistration)
    }

    suspend fun update(placeReviewModification: PlaceReviewModification): Response<Unit> {
        return service.update(placeReviewModification)
    }

    suspend fun delete(placeReviewId: Long): Response<Unit> {
        return service.delete(placeReviewId)
    }

    suspend fun getByPlaceId(info: PlaceReviewByPlaceIdQuery): PlaceReviewDTO =
        service.getReviewsByPlaceId(
            placeId = info.placeId,
            size = info.size,
            sortType = info.sortType,
            lastPlaceReviewId = info.lastPlaceReviewId,
        )

    suspend fun getByUserId(info: PlaceReviewByUserIdQuery): PlaceReviewDTO =
        service.getReviewsByUserId(
            userId = info.userId,
            size = info.size,
            sortType = info.sortType,
            lastPlaceReviewId = info.lastPlaceReviewId,
        )
    suspend fun isExistReview(placeId: String): IsExistPlaceReviewDTO {
        return service.isExistReview(placeId)
    }

    suspend fun getReviewCount(placeId: String): PlaceReviewCountDTO {
        return service.getReviewCount(placeId)
    }
}
