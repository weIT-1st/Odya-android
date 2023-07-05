package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    @field:Json(name = "userID") val userID: Int?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "phoneNumber") val phoneNumber: String?,
    @field:Json(name = "gender") val gender: String,
    @field:Json(name = "birthday") val birthday: String,
    @field:Json(name = "socialType") val socialType: String,
)
