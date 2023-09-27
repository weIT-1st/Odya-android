package com.weit.domain.model.place

data class PlaceReviewByPlaceIdQuery(
    val placeId: String,
    val size: Int = 10,
    val sortType: String = "LATEST",
    val lastPlaceReviewId: Long? = null,
)
