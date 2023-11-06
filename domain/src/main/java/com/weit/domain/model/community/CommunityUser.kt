package com.weit.domain.model.community

import com.weit.domain.model.user.UserProfile


interface CommunityUser {
    val userId: Long
    val nickname: String
    val profile: UserProfile
    val isFollowing: Boolean?
}
