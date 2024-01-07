package com.weit.presentation.model.profile.lifeshot

import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics

data class ProfileUserInfo(
    val user: User,
    val userStatistics: UserStatistics,
)
