package com.weit.domain.model.place

import java.time.LocalDate
import java.time.LocalDateTime

data class PlaceReviewDetail(
    val id: Long,
    val placeId: String,
    val userId: Long,
    val writerNickname: String,
    val starRating: Int,
    val review: String,
    val createdAt: LocalDate
)
