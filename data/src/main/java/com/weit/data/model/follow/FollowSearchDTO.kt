package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowSearchDTO(
    @field:Json(name = "hasNext") val hasNext: Boolean,
    @field:Json(name = "content") val content: List<FollowUserContentDTO>
)