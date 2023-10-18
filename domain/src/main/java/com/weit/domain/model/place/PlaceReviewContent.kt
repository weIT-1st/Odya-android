package com.weit.domain.model.place

import java.time.LocalDateTime

data class PlaceReviewContent(
    val placeReviewId: Long,
    val placeId: String,
    val userInfo: UserInfo,
    val starRating: Int,
    val review: String,
    val createdAt: LocalDateTime,
)
