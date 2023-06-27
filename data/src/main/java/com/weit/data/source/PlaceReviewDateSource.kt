package com.weit.data.source

import com.weit.data.service.PlaceReviewService
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import javax.inject.Inject

class PlaceReviewDateSource @Inject constructor(
    private val service: PlaceReviewService,
) {

    suspend fun register(info: PlaceReviewRegistrationInfo) {
        service.register(
            placeId = info.placeId,
            rating = info.rating,
            review = info.review
        )
    }

    suspend fun update(info: PlaceReviewUpdateInfo) {
        service.update(
            id = info.id,
            rating = info.rating,
            review = info.review
        )
    }

    suspend fun delete(id: Long) {
        service.delete(id)
    }
}
