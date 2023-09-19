package com.weit.domain.model.place

import com.weit.domain.model.user.UserProfile
import java.time.LocalDate

data class PlaceReviewDetail(
    val id: Long,
    val placeId: String,
    val userId: Long,
    val writerNickname: String,
    val starRating: Int,
    val review: String,
    val createdAt: LocalDate,
    val profile: UserProfile,
)
