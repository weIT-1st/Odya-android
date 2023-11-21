package com.weit.data.model.follow

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommunityId(
    @field:Json(name = "communityId") val communityId: Long,
)
