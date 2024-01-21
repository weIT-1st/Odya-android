package com.weit.domain.model.follow

import com.weit.domain.model.user.UserProfile

interface FollowUserContent {
    val userId: Long
    val nickname: String
    val profile: UserProfile
    val isFollowing: Boolean
}
