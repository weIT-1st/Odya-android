package com.weit.data.model.topic

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.topic.TopicDetail

@JsonClass(generateAdapter = true)
data class TopicDTO(
    @field:Json(name = "id") override val topicId: Long,
    @field:Json(name = "word") override val topicWord: String,
) : TopicDetail
