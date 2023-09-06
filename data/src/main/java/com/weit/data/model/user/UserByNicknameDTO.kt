package com.weit.data.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserByNicknameDTO(
    @field:Json(name = "hasNext") val hasNext: Boolean,
    @field:Json(name = "content") val content: List<UserContentDTO>
)
