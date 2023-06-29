package com.weit.data.model.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserRegistration(
    @field:Json(name = "username") val name: String,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "phoneNumber") val phoneNumber: String?,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "gender") val gender: String,
    @field:Json(name = "birthday") val birthday: List<Int>,
)
