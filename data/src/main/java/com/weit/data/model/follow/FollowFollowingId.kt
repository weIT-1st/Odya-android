package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowFollowingId(
    @field:Json(name = "followingId") val followingId: Long,
)
