package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExperiencedFriendDTO(
    @field:Json(name = "count") val count: Int,
    @field:Json(name = "followings") val followings: List<FollowUserContentDTO>,
)
