package com.weit.data.model.place

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.data.model.user.UserProfileDTO

@JsonClass(generateAdapter = true)
data class UserInfoDTO(
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profile") val profile: UserProfileDTO,
)
