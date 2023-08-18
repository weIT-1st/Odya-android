package com.weit.data.model.topic

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopicDTO(
    @field:Json(name = "id") val topicId: Long,
    @field:Json(name = "word") val topicWord: String,
)
