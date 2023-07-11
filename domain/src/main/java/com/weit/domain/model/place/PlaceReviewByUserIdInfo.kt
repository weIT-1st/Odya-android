package com.weit.domain.model.place

data class PlaceReviewByUserIdInfo(
    val userId: Long,
    val size: Int,
    val sortType: String? = null,
    val lastPlaceReviewId: Long? = null,
)
