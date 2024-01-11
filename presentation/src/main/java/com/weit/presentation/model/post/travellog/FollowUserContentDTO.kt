package com.weit.presentation.model.post.travellog

import android.os.Parcelable
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.model.user.UserProfileDTO
import com.weit.presentation.model.user.toDTO
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowUserContentDTO(
    override val userId: Long,
    override val nickname: String,
    override val profile: UserProfileDTO,
    override val isFollowing: Boolean,
) : FollowUserContent, Parcelable

internal fun FollowUserContent.toDTO() =
    FollowUserContentDTO(userId, nickname, profile.toDTO(),isFollowing)
