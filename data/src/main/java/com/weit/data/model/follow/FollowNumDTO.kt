package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowNumDTO(
    @field:Json(name = "followingCount") val followingCount : Long,
    @field:Json(name = "followerCount") val followerCount : Long,
)
