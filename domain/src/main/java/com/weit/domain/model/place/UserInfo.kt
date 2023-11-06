package com.weit.domain.model.place

import com.weit.domain.model.user.UserProfile

data class UserInfo(
    val userId: Long,
    val nickname: String,
    val profile: UserProfile,
)
