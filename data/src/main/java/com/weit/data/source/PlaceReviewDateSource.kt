package com.weit.data.source

import com.weit.data.model.place.IsExistPlaceReviewDTO
import com.weit.data.model.place.PlaceReviewCountDTO
import com.weit.data.model.place.PlaceReviewListDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.service.PlaceReviewService
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import retrofit2.Response
import javax.inject.Inject

class PlaceReviewDateSource @Inject constructor(
    private val service: PlaceReviewService,
) {

    suspend fun register(placeReviewRegistration: PlaceReviewRegistration): Unit {
        return service.register(placeReviewRegistration)
    }

    suspend fun update(placeReviewModification: PlaceReviewModification): Unit {
        return service.update(placeReviewModification)
    }

    suspend fun delete(placeReviewId: Long) {
        service.delete(placeReviewId)
    }

    suspend fun getByPlaceId(info: PlaceReviewByPlaceIdInfo): PlaceReviewListDTO =
        service.getReviewsByPlaceId(
            placeId = info.placeId,
            size = info.size,
            sortType = info.sortType,
            lastPlaceReviewId = info.lastPlaceReviewId,
        )

    suspend fun getByUserId(info: PlaceReviewByUserIdInfo): PlaceReviewListDTO =
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
