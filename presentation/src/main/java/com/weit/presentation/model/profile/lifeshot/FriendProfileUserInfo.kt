package com.weit.presentation.model.profile.lifeshot

import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics

data class FriendProfileUserInfo(
    val user: SearchUserContent,
    val userStatistics: UserStatistics,
)
