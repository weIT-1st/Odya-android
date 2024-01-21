package com.weit.domain.model.place

import java.time.LocalDateTime

data class PlaceMyReviewInfo(
    val placeName: String,
    val rating: Float,
    val review: String,
    val createAt: LocalDateTime,
    val placeReviewId: Long,
)
