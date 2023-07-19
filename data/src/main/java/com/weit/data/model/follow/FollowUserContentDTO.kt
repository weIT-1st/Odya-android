package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowUserContentDTO(
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "nickname") val nickname: String,
    @field:Json(name = "profileName") val profileName: String,
)
