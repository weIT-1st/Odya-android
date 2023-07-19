package com.weit.domain.model.place

data class PlaceReviewByPlaceIdInfo(
    val placeId: String,
    val size: Int,
    val sortType: String? = null,
    val lastPlaceReviewId: Long? = null,
)
