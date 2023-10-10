package com.weit.data.model.community

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommunityRegistration(
    @field:Json(name = "content") val content: String,
    @field:Json(name = "visibility") val visibility: String,
    @field:Json(name = "placeId") val placeId: String?,
    @field:Json(name = "travelJournalId") val travelJournalId: Long?,
    @field:Json(name = "topicId") val topicId: Long?,
)
