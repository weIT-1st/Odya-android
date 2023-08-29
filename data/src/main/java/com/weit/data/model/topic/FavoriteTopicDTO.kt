package com.weit.data.model.topic

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.weit.domain.model.topic.TopicDetail

@JsonClass(generateAdapter = true)
data class FavoriteTopicDTO(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "userId") val userId: Long,
    @field:Json(name = "topicId") override val topicId: Long,
    @field:Json(name = "topicWord") override val topicWord: String,
) : TopicDetail
