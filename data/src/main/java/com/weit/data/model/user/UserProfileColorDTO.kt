package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.user.UserProfileColor

@JsonClass(generateAdapter = true)
data class UserProfileColorDTO(
    @field:Json(name = "colorHex") override val colorHex: String,
    @field:Json(name = "red") override val red: Int,
    @field:Json(name = "green") override val green: Int,
    @field:Json(name = "blue") override val blue: Int,
) : UserProfileColor
