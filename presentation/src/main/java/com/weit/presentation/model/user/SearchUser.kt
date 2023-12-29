package com.weit.presentation.model.user

import android.os.Parcelable
import com.weit.domain.model.community.CommunityUser
import com.weit.domain.model.user.UserProfile
import kotlinx.parcelize.Parcelize


data class SearchUser(
    val userId: Long,
    val nickname: String,
    val profile: UserProfile,
)
