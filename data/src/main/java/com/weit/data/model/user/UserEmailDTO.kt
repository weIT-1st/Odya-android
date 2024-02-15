package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.user.User

@JsonClass(generateAdapter = true)
data class UserEmailDTO(
    @field:Json(name = "email") val email: String,
)
