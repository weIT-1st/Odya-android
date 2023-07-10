package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowFollowingIdDTO(
    @field:Json(name = "userId") val userId: Long
)

