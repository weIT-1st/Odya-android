package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.user.User

@JsonClass(generateAdapter = true)
data class UserDTO(
    @field:Json(name = "userId") override val userId: Long,
    @field:Json(name = "email") override val email: String?,
    @field:Json(name = "nickname") override val nickname: String,
    @field:Json(name = "phoneNumber") override val phoneNumber: String?,
    @field:Json(name = "gender") override val gender: String,
    @field:Json(name = "birthday") override val birthday: String,
    @field:Json(name = "socialType") override val socialType: String,
    @field:Json(name = "profile") override val profile: UserProfileDTO,
) : User
