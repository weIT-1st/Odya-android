package com.weit.domain.model.bookmark

import com.weit.domain.model.user.UserProfile


data class Writer (
    val userId: Long,
    val nickname: String,
    val profile: UserProfile,
    val isFollowing: Boolean,
)
