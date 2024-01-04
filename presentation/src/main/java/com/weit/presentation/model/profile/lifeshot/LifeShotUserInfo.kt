package com.weit.presentation.model.profile.lifeshot

import android.os.Parcelable
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics
import kotlinx.parcelize.Parcelize

data class LifeShotUserInfo(
    val user: User,
    val userStatistics: UserStatistics,
)
