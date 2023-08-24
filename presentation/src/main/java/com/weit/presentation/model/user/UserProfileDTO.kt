package com.weit.presentation.model.user

import android.os.Parcelable
import com.weit.domain.model.user.UserProfile
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfileDTO(
    override val url: String,
    override val color: UserProfileColorDTO?,
) : UserProfile, Parcelable

internal fun UserProfile.toDTO() =
    UserProfileDTO(url, color?.toDTO())
