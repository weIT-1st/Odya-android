package com.weit.domain.model.user

import com.weit.domain.model.user.UserProfile

interface SearchUserContent {
    val userId: Long
    val nickname: String
    val profile: UserProfile
    val isFollowing: Boolean
}
