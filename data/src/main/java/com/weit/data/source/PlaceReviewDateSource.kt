package com.weit.data.source

import com.weit.data.model.place.PlaceReviewListDTO
import com.weit.data.model.place.PlaceReviewModification
import com.weit.data.model.place.PlaceReviewRegistration
import com.weit.data.service.PlaceReviewService
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewByUserIdInfo
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import javax.inject.Inject

class PlaceReviewDateSource @Inject constructor(
    private val service: PlaceReviewService,
) {

    suspend fun register(placeReviewRegistration: PlaceReviewRegistration) {
        service.register(placeReviewRegistration)
    }

    suspend fun update(placeReviewModification: PlaceReviewModification) {
        service.update(placeReviewModification)
    }

    suspend fun delete(placeReviewId: Long) {
        service.delete(placeReviewId)
    }

    suspend fun getByPlaceId(info: PlaceReviewByPlaceIdInfo): PlaceReviewListDTO =
        service.getReviewsByPlaceId(
            id = info.placeId,
            startId = info.startId,
            count = info.count,
        )

    suspend fun getByUserId(info: PlaceReviewByUserIdInfo): PlaceReviewListDTO =
        service.getReviewsByUserId(
            id = info.userId,
            startId = info.startId,
            count = info.count,
        )
}
