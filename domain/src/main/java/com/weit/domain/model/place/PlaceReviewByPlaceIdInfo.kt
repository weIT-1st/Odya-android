package com.weit.domain.model.place

data class PlaceReviewByPlaceIdInfo(
    val placeId: String,
    val startId: Long? = null,
    val count: Int
)