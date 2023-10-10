package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommunityCommentRegistration(
    @field:Json(name = "content") val content: String,
)
