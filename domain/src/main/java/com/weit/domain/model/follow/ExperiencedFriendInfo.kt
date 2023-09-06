package com.weit.domain.model.follow

import com.weit.domain.model.user.UserProfile

data class ExperiencedFriendInfo(
    val userId: Long,
    val nickname: String,
    val profile: UserProfile
)
