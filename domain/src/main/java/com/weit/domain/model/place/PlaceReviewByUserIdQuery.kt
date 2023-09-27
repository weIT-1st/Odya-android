package com.weit.domain.model.place

data class PlaceReviewByUserIdQuery(
    val userId: Long,
    val size: Int = 10,
    val sortType: String? = null,
    val lastPlaceReviewId: Long? = null,
)
