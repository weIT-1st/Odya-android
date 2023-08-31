package com.weit.data.model.topic

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopicRegistration(
    @field:Json(name = "topicIdList") val topicIdList: List<Long>,
)
