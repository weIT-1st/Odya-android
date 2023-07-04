package com.weit.domain.model.place

data class PlaceReviewUpdateInfo(
    val placeReviewId: Long,
    val rating: Int,
    val review: String,
)
