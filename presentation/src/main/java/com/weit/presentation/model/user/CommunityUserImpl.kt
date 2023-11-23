package com.weit.presentation.model.user

import android.os.Parcelable
import com.weit.domain.model.community.CommunityUser
import com.weit.domain.model.user.UserProfile
import kotlinx.parcelize.Parcelize


data class CommunityUserImpl(
    override val userId: Long,
    override val nickname: String,
    override val profile: UserProfile,
    override val isFollowing: Boolean
) : CommunityUser
