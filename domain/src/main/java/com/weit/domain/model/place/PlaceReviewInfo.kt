package com.weit.domain.model.place

import com.weit.domain.model.user.UserProfile

data class PlaceReviewInfo (
    val writerNickname: String,
    val rating: Float,
    val review: String,
    val createAt: String,
    val userId: Long,
    var isMine: Boolean,
    val placeReviewId : Long,
    val profile: UserProfile
        )
