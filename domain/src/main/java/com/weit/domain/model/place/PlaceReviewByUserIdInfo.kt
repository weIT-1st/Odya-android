package com.weit.domain.model.place

data class PlaceReviewByUserIdInfo(
    val userId: Long,
    val startId: Long? = null,
    val count: Int
)