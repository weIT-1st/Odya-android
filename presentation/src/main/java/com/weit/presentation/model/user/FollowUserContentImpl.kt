package com.weit.presentation.model.user

import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.user.UserProfile

data class FollowUserContentImpl (
    override val userId: Long,
    override val nickname: String,
    override val profile: UserProfile,
    override val isFollowing: Boolean
) : FollowUserContent
