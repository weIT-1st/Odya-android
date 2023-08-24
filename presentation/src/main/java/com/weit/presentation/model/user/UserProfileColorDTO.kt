package com.weit.presentation.model.user

import android.os.Parcelable
import com.weit.domain.model.user.UserProfileColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfileColorDTO(
    override val colorHex: String,
    override val red: Int,
    override val green: Int,
    override val blue: Int,
) : UserProfileColor, Parcelable

internal fun UserProfileColor.toDTO() =
    UserProfileColorDTO(colorHex, red, green, blue)
