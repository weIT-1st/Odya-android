package com.weit.domain.model.follow

import com.weit.domain.model.user.UserProfile

data class  ExperiencedFriendInfo(
    val count: Int,
    val followings: List<ExperiencedFriendContent>?
)
