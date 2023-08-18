package com.weit.data.model.topic

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteTopicDTO(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "topicId") val topicId: Long,
    @field:Json(name = "topicWord") val topicWord: String,
)
